package insight.drivers

import insight.utilities.log.logError
import insight.utilities.log.logInfo
import insight.utilities.log.logWarn
import insight.utilities.Messages
import insight.utilities.props.PropertyFactory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Thread-local WinAppDriver factory. Mirrors the web framework's Driver object:
 * the driver is created via [createAndStoreDriver], fetched with [getMyDriver]
 * and torn down with [closeDriver] (driven by the Cucumber hooks/steps).
 */
object DesktopDriver {

    private const val WINAPPDRIVER_START_TIMEOUT_MS = 15_000L
    private const val WINDOW_ATTACH_TIMEOUT_MS = 30_000L
    private const val WINDOW_POLL_INTERVAL_MS = 1_000L
    private const val STATUS_PROBE_TIMEOUT_MS = 1_000
    // WinAppDriver waits this many seconds for a freshly launched app to become responsive
    private const val APP_LAUNCH_WAIT_SECONDS = "10"
    private const val E_FAIL_EXIT_CODE = -2147467259

    private val driverThreadLocal: ThreadLocal<WindowsDriver<WebElement>> = ThreadLocal()
    private val launchedAppThreadLocal: ThreadLocal<String> = ThreadLocal()

    /** Only set when this process started WinAppDriver, so we never kill one the user started. */
    @Volatile
    private var winAppDriverProcess: Process? = null

    private val desktopConfig get() = PropertyFactory.desktopProperty()

    // Classic apps launched directly from an executable path. The paths are resolved lazily per
    // app so a missing property for one app cannot break a run that never uses it.
    private val appPaths: Map<String, () -> String> = mapOf(
        "notepad" to { desktopConfig.notepadPath() },
        "clink" to { desktopConfig.clinkPath() }
    )

    // Apps launched via UWP appId that require window-handle attachment after launch
    private val uwpApps: Map<String, () -> Pair<String, String>> = mapOf(
        "ms teams" to { desktopConfig.msTeamsAppId() to desktopConfig.msTeamsWindowTitle() }
    )

    init {
        // A crashed or interrupted run would otherwise leave our WinAppDriver.exe behind.
        Runtime.getRuntime().addShutdownHook(Thread({ stopWinAppDriverIfOwned() }, "winappdriver-shutdown"))
    }

    fun getMyDriver(): WindowsDriver<WebElement> =
        driverThreadLocal.get() ?: throw IllegalStateException("Desktop driver not initialized for this thread!")

    fun isDriverInitialized(): Boolean = driverThreadLocal.get() != null

    fun sessionId(): String? = driverThreadLocal.get()?.sessionId?.toString()

    /** The app name this thread's driver was created for, or null if there is no driver. */
    fun currentApp(): String? = launchedAppThreadLocal.get()

    fun createAndStoreDriver(appName: String): WindowsDriver<WebElement> {
        val normalised = appName.lowercase(Locale.ROOT).trim()

        driverThreadLocal.get()?.let { existing ->
            val current = launchedAppThreadLocal.get()
            if (current != null && current != normalised) {
                logWarn("Driver for '$current' is already open on this thread; reusing it for '$normalised'. Close it first if a separate app session was intended.")
            }
            return existing
        }

        startWinAppDriverIfNeeded()

        val startedAt = System.currentTimeMillis()
        val driver = when {
            uwpApps.containsKey(normalised) -> launchUwpAndAttach(normalised)
            appPaths.containsKey(normalised) -> launchApp(appPaths.getValue(normalised)())
            else -> throw IllegalArgumentException(
                "App '$appName' not configured. Known apps: ${(appPaths.keys + uwpApps.keys).sorted()}"
            )
        }

        // A window that refuses to maximize is not a reason to fail the scenario
        runCatching { driver.manage().window().maximize() }
            .onFailure { logWarn("Could not maximize '$normalised' window: ${it.message}") }

        driverThreadLocal.set(driver)
        launchedAppThreadLocal.set(normalised)
        logInfo("Ready: $appName (session ${driver.sessionId}, ${System.currentTimeMillis() - startedAt}ms)")
        return driver
    }

    fun closeDriver() {
        try {
            driverThreadLocal.get()?.quit()
        } catch (e: Exception) {
            logError("Error while quitting desktop driver: ${e.message}")
        } finally {
            driverThreadLocal.remove()
            launchedAppThreadLocal.remove()
        }
    }

    // ---------- Launching ----------

    private fun launchApp(path: String): WindowsDriver<WebElement> {
        require(File(path).exists()) { "Application executable not found at: $path" }

        val capabilities = DesiredCapabilities().apply {
            setCapability("app", path)
            setCapability("platformName", "Windows")
            setCapability("deviceName", "WindowsPC")
            // Let WinAppDriver wait for the splash/startup to settle instead of failing immediately
            setCapability("ms:waitForAppLaunch", APP_LAUNCH_WAIT_SECONDS)
        }
        return WindowsDriver(URL(desktopConfig.winAppDriverUrl()), capabilities)
    }

    private fun launchUwpAndAttach(normalisedAppName: String): WindowsDriver<WebElement> {
        val (appId, windowTitle) = uwpApps.getValue(normalisedAppName)()

        // Launch the app via shell - bypasses WinAppDriver's process tracking entirely
        ProcessBuilder("cmd", "/c", "start", "", "shell:AppsFolder\\$appId")
            .redirectErrorStream(true)
            .start()
            .waitFor(10, TimeUnit.SECONDS)
        logInfo("Launched UWP app: $appId, waiting for window...")

        val hexHandle = waitForWindowHandle(windowTitle, WINDOW_ATTACH_TIMEOUT_MS)
        logInfo("Attached to '$windowTitle' handle: $hexHandle")

        val capabilities = DesiredCapabilities().apply {
            setCapability("appTopLevelWindow", hexHandle)
            setCapability("platformName", "Windows")
            setCapability("deviceName", "WindowsPC")
        }
        return WindowsDriver(URL(desktopConfig.winAppDriverUrl()), capabilities)
    }

    // ---------- Window handle lookup ----------

    private fun waitForWindowHandle(windowTitle: String, timeoutMs: Long): String {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            resolveWindowHandle(windowTitle)?.let { return it }
            Thread.sleep(WINDOW_POLL_INTERVAL_MS)
        }
        throw IllegalStateException("Timed out waiting for window '$windowTitle' to appear within ${timeoutMs}ms")
    }

    /**
     * Finds the first visible top-level window whose title contains [windowTitle] and returns its
     * handle as the hex string WinAppDriver's `appTopLevelWindow` capability expects.
     *
     * This calls EnumWindows through JNA rather than shelling out to PowerShell: it runs in-process
     * in about a millisecond instead of ~600ms per poll, and there is no script, temp file or
     * output parsing to get wrong.
     */
    private fun resolveWindowHandle(windowTitle: String): String? {
        var found: HWND? = null

        val callback = object : WNDENUMPROC {
            override fun callback(hWnd: HWND, data: Pointer?): Boolean {
                if (!titleOf(hWnd).contains(windowTitle, ignoreCase = true)) return true
                if (!User32.INSTANCE.IsWindowVisible(hWnd)) return true
                found = hWnd
                return false // stop enumerating
            }
        }
        User32.INSTANCE.EnumWindows(callback, null)

        val handle = found?.let { Pointer.nativeValue(it.pointer) } ?: return null
        return if (handle == 0L) null else java.lang.Long.toHexString(handle).uppercase(Locale.ROOT)
    }

    private fun titleOf(hWnd: HWND): String {
        val length = User32.INSTANCE.GetWindowTextLength(hWnd)
        if (length <= 0) return ""
        val buffer = CharArray(length + 1)
        User32.INSTANCE.GetWindowText(hWnd, buffer, buffer.size)
        return Native.toString(buffer)
    }

    // ---------- WinAppDriver process ----------

    private fun startWinAppDriverIfNeeded() {
        if (isWinAppDriverRunning()) {
            logInfo("WinAppDriver already running.")
            return
        }
        logInfo("Starting WinAppDriver...")

        val winAppDriverPath = desktopConfig.winAppDriverPath()
        if (!File(winAppDriverPath).exists()) {
            throw IllegalStateException(
                Messages.of("winappdriver-not-installed", "winAppDriverPath" to winAppDriverPath)
            )
        }

        val logFile = File.createTempFile("winappdriver_log_", ".txt").apply { deleteOnExit() }
        logInfo("WinAppDriver logs will be written to: ${logFile.absolutePath}")

        val process = ProcessBuilder(winAppDriverPath)
            .redirectOutput(logFile)
            .redirectError(logFile)
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .start()
        winAppDriverProcess = process

        val deadline = System.currentTimeMillis() + WINAPPDRIVER_START_TIMEOUT_MS
        while (!isWinAppDriverRunning()) {
            if (!process.isAlive) {
                winAppDriverProcess = null
                throw IllegalStateException(startupFailureMessage(process.exitValue(), winAppDriverPath, logFile))
            }
            if (System.currentTimeMillis() > deadline) {
                throw IllegalStateException(
                    Messages.of(
                        "winappdriver-start-timeout",
                        "url" to desktopConfig.winAppDriverUrl(),
                        "timeoutMs" to WINAPPDRIVER_START_TIMEOUT_MS,
                        "logs" to readLog(logFile)
                    )
                )
            }
            Thread.sleep(500)
        }
        logInfo("WinAppDriver started successfully")
    }

    /** Stops WinAppDriver only if this process started it. Safe to call more than once. */
    fun stopWinAppDriverIfOwned() {
        val process = winAppDriverProcess ?: return
        winAppDriverProcess = null
        runCatching {
            process.destroy()
            if (!process.waitFor(5, TimeUnit.SECONDS)) process.destroyForcibly()
        }.onFailure { logError("Error stopping WinAppDriver: ${it.message}") }
    }

    private fun startupFailureMessage(exitCode: Int, winAppDriverPath: String, logFile: File): String =
        if (exitCode == E_FAIL_EXIT_CODE) {
            Messages.of(
                "winappdriver-not-elevated",
                "exitCode" to exitCode,
                "winAppDriverPath" to winAppDriverPath
            )
        } else {
            Messages.of(
                "winappdriver-start-failed",
                "exitCode" to exitCode,
                "logs" to readLog(logFile)
            )
        }

    private fun readLog(logFile: File): String =
        runCatching { logFile.readText() }.getOrElse { "Could not read log file: ${it.message}" }

    private fun isWinAppDriverRunning(): Boolean {
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL("${desktopConfig.winAppDriverUrl().trimEnd('/')}/status").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = STATUS_PROBE_TIMEOUT_MS
                readTimeout = STATUS_PROBE_TIMEOUT_MS
            }
            connection.responseCode == 200
        } catch (e: Exception) {
            false
        } finally {
            connection?.disconnect() // otherwise each poll leaks a keep-alive socket
        }
    }
}
