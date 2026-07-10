package insight.drivers

import insight.utilities.log.logError
import insight.utilities.log.logInfo
import insight.utilities.props.PropertyFactory
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

/**
 * Thread-local WinAppDriver factory. Mirrors the web framework's Driver object:
 * the driver is created via [createAndStoreDriver], fetched with [getMyDriver]
 * and torn down with [closeDriver] (driven by the Cucumber hooks/steps).
 */
object DesktopDriver {

    private val driverThreadLocal: ThreadLocal<WindowsDriver<WebElement>> = ThreadLocal()
    private var winAppDriverProcess: Process? = null

    private val desktopConfig get() = PropertyFactory.desktopProperty()

    // Classic apps launched directly from an executable path
    private val appPaths: Map<String, String> by lazy {
        mapOf(
            "notepad" to desktopConfig.notepadPath(),
            "clink" to desktopConfig.clinkPath()
        )
    }

    // Apps launched via UWP appId that require window-handle attachment after launch
    private val uwpApps: Map<String, Pair<String, String>> by lazy {
        mapOf(
            "ms teams" to (desktopConfig.msTeamsAppId() to desktopConfig.msTeamsWindowTitle())
        )
    }

    fun getMyDriver(): WindowsDriver<WebElement> =
        driverThreadLocal.get() ?: throw IllegalStateException("Desktop driver not initialized for this thread!")

    fun isDriverInitialized(): Boolean = driverThreadLocal.get() != null

    fun sessionId(): String? = driverThreadLocal.get()?.sessionId?.toString()

    fun createAndStoreDriver(appName: String): WindowsDriver<WebElement> {
        var currentDriver = driverThreadLocal.get()
        if (currentDriver == null) {
            startWinAppDriverIfNeeded()

            val normalised = appName.lowercase(Locale.getDefault())

            currentDriver = when {
                uwpApps.containsKey(normalised) -> launchUwpAndAttach(normalised)
                appPaths.containsKey(normalised) -> launchApp(appPaths[normalised]!!)
                else -> throw IllegalArgumentException("App '$appName' not configured")
            }

            currentDriver.manage().window().maximize()
            driverThreadLocal.set(currentDriver)
            logInfo("Ready: $appName")
        }
        return currentDriver
    }

    fun closeDriver() {
        try {
            driverThreadLocal.get()?.quit()
        } catch (e: Exception) {
            logError("Error while quitting desktop driver: ${e.message}")
        } finally {
            driverThreadLocal.remove()
        }
    }

    private fun launchApp(path: String): WindowsDriver<WebElement> {
        val capabilities = DesiredCapabilities().apply {
            setCapability("app", path)
            setCapability("platformName", "Windows")
            setCapability("deviceName", "WindowsPC")
        }
        return WindowsDriver(URL(desktopConfig.winAppDriverUrl()), capabilities)
    }

    private fun launchUwpAndAttach(normalisedAppName: String): WindowsDriver<WebElement> {
        val (appId, windowTitle) = uwpApps[normalisedAppName]!!

        // Launch the app via shell - bypasses WinAppDriver's process tracking entirely
        Runtime.getRuntime().exec(arrayOf("cmd", "/c", "start", "shell:AppsFolder\\$appId"))
        logInfo("Launched UWP app: $appId, waiting for window...")

        // Poll until the window is visible, up to 30 seconds
        val hexHandle = waitForWindowHandle(windowTitle, timeoutMs = 30_000)
        logInfo("Attached to '$windowTitle' handle: $hexHandle")

        val capabilities = DesiredCapabilities().apply {
            setCapability("appTopLevelWindow", hexHandle)
            setCapability("platformName", "Windows")
            setCapability("deviceName", "WindowsPC")
        }
        return WindowsDriver(URL(desktopConfig.winAppDriverUrl()), capabilities)
    }

    private fun waitForWindowHandle(windowTitle: String, timeoutMs: Long): String {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            val handle = resolveWindowHandleViaProcess(windowTitle)
            if (handle != null) return handle
            Thread.sleep(1000)
        }
        throw IllegalStateException("Timed out waiting for window '$windowTitle' to appear")
    }

    private fun resolveWindowHandleViaProcess(windowTitle: String): String? {
        val scriptFile = createTempFile("wad_find_window", ".ps1").apply {
            deleteOnExit()
            bufferedWriter().use { w ->
                w.write("""
Add-Type @"
using System;
using System.Runtime.InteropServices;
using System.Text;
public class Win32 {
    [DllImport("user32.dll")]
    public static extern bool EnumWindows(EnumWindowsProc enumProc, IntPtr lParam);
    public delegate bool EnumWindowsProc(IntPtr hWnd, IntPtr lParam);
    [DllImport("user32.dll")]
    public static extern int GetWindowText(IntPtr hWnd, StringBuilder lpString, int nMaxCount);
    [DllImport("user32.dll")]
    public static extern bool IsWindowVisible(IntPtr hWnd);
}
"@
""".trimIndent())
                w.newLine()
                w.write("\$found = \$null")
                w.newLine()
                w.write("[Win32]::EnumWindows(")
                w.newLine()
                w.write("    [Win32+EnumWindowsProc]{")
                w.newLine()
                w.write("        param(\$hwnd, \$lp)")
                w.newLine()
                w.write("        \$sb = New-Object System.Text.StringBuilder 256")
                w.newLine()
                w.write("        [Win32]::GetWindowText(\$hwnd, \$sb, 256) | Out-Null")
                w.newLine()
                w.write("        if ([Win32]::IsWindowVisible(\$hwnd) -and \$sb.ToString() -match '$windowTitle') {")
                w.newLine()
                w.write("            \$script:found = \$hwnd.ToInt64().ToString('X')")
                w.newLine()
                w.write("            return \$false")
                w.newLine()
                w.write("        }")
                w.newLine()
                w.write("        return \$true")
                w.newLine()
                w.write("    },")
                w.newLine()
                w.write("    [IntPtr]::Zero")
                w.newLine()
                w.write(")")
                w.newLine()
                w.write("if (\$found) { Write-Output \$found } else { Write-Output 'NOT_FOUND' }")
                w.newLine()
            }
        }

        val process = ProcessBuilder(
            "powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", scriptFile.absolutePath
        )
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText().trim()
        process.waitFor()
        scriptFile.delete()

        if (output.contains("NOT_FOUND")) return null

        val hwndLine = output.lines()
            .map { it.trim() }
            .lastOrNull { it.matches(Regex("[0-9A-Fa-f]+")) }
            ?: return null

        val decimal = hwndLine.toLong(16)
        return if (decimal == 0L) null else hwndLine
    }

    private fun startWinAppDriverIfNeeded() {
        if (isWinAppDriverRunning()) {
            logInfo("WinAppDriver already running.")
            return
        }
        logInfo("Starting WinAppDriver...")

        val winAppDriverPath = desktopConfig.winAppDriverPath()
        val file = File(winAppDriverPath)
        if (!file.exists()) {
            throw RuntimeException(
                "WinAppDriver.exe not found at path: $winAppDriverPath. Please ensure Windows Application Driver is installed. " +
                    "(Download from https://github.com/microsoft/WinAppDriver/releases)"
            )
        }

        val logFile = File.createTempFile("winappdriver_log_", ".txt").apply { deleteOnExit() }
        logInfo("WinAppDriver logs will be written to: ${logFile.absolutePath}")

        val processBuilder = ProcessBuilder(winAppDriverPath)
        processBuilder.redirectOutput(logFile)
        processBuilder.redirectError(logFile)
        processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE)

        val process = processBuilder.start()
        winAppDriverProcess = process

        val timeoutMs = 5000
        val startTime = System.currentTimeMillis()
        while (!isWinAppDriverRunning()) {
            if (!process.isAlive) {
                val exitCode = process.exitValue()
                val logContent = try { logFile.readText() } catch (e: Exception) { "Could not read log file: ${e.message}" }

                val errorMessage = if (exitCode == -2147467259) {
                    "WinAppDriver process terminated early with exit code -2147467259 (0x80004005: E_FAIL).\n" +
                        "This occurs because WinAppDriver lacks administrative privileges to bind to its port.\n" +
                        "To resolve this, please do one of the following:\n" +
                        "  1. Start WinAppDriver manually as Administrator: Open Command Prompt or PowerShell as Administrator and run:\n" +
                        "     \"$winAppDriverPath\"\n" +
                        "     Keep it running in the background. The tests will automatically detect it.\n" +
                        "  2. Run your IDE (IntelliJ IDEA) or Terminal as Administrator so programmatically spawned processes are elevated.\n" +
                        "  3. Enable Windows Developer Mode in your Windows Settings (Settings -> For Developers -> Developer Mode = ON)."
                } else {
                    "WinAppDriver process terminated early with exit code $exitCode.\nLogs:\n$logContent"
                }
                throw RuntimeException(errorMessage)
            }
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                val logContent = try { logFile.readText() } catch (e: Exception) { "Could not read log file: ${e.message}" }
                throw RuntimeException("WinAppDriver did not start in time.\nLogs:\n$logContent")
            }
            Thread.sleep(500)
        }
        logInfo("WinAppDriver started successfully")
    }

    private fun isWinAppDriverRunning(): Boolean {
        return try {
            val url = URL("${desktopConfig.winAppDriverUrl().trimEnd('/')}/status")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 1000
            conn.responseCode == 200
        } catch (e: Exception) {
            false
        }
    }
}
