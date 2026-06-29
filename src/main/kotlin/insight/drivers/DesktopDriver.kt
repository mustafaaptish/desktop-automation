package insight.drivers

import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import java.net.URL
import java.util.*

object DesktopDriver {

    private var driver: WindowsDriver<WebElement>? = null
    private var winAppDriverProcess: Process? = null

    private val appPaths = mapOf(
        "notepad" to "C:\\Windows\\System32\\notepad.exe",
        "clink" to "C:\\Program Files (x86)\\Clearview Intelligence\\CLink\\CLink.exe"
    )

    // Apps launched via UWP appId but require window-handle attachment after launch
    private val uwpApps = mapOf(
        "ms teams" to "MSTeams_8wekyb3d8bbwe!MSTeams"
    )

    // Partial window title used to locate the window after launch
    private val uwpWindowTitles = mapOf(
        "ms teams" to "Microsoft Teams"
    )

    fun getDriver(): WindowsDriver<WebElement> {
        return driver ?: throw IllegalStateException("Desktop driver not initialized!")
    }

    fun startApp(appName: String): WindowsDriver<WebElement> {
        if (driver == null) {
            startWinAppDriverIfNeeded()

            val normalised = appName.lowercase(Locale.getDefault())

            driver = when {
                uwpApps.containsKey(normalised) -> launchUwpAndAttach(normalised)
                appPaths.containsKey(normalised) -> launchApp(appPaths[normalised]!!)
                else -> throw IllegalArgumentException("App '$appName' not configured")
            }

            driver!!.manage().window().maximize()
            println("✅ Ready: $appName")
        }
        return driver!!
    }

    private fun launchApp(path: String): WindowsDriver<WebElement> {
        val capabilities = DesiredCapabilities().apply {
            setCapability("app", path)
            setCapability("platformName", "Windows")
            setCapability("deviceName", "WindowsPC")
        }
        return WindowsDriver(URL("http://127.0.0.1:4723"), capabilities)
    }

    private fun launchUwpAndAttach(normalisedAppName: String): WindowsDriver<WebElement> {
        val appId = uwpApps[normalisedAppName]!!
        val windowTitle = uwpWindowTitles[normalisedAppName]!!

        // Launch the app via shell - bypasses WinAppDriver's process tracking entirely
        Runtime.getRuntime().exec(arrayOf("cmd", "/c", "start", "shell:AppsFolder\\$appId"))
        println("⏳ Launched UWP app: $appId, waiting for window...")

        // Poll until the window is visible, up to 30 seconds
        val hexHandle = waitForWindowHandle(windowTitle, timeoutMs = 30_000)
        println("✅ Attached to '$windowTitle' handle: $hexHandle")

        val capabilities = DesiredCapabilities().apply {
            setCapability("appTopLevelWindow", hexHandle)
            setCapability("platformName", "Windows")
            setCapability("deviceName", "WindowsPC")
        }
        return WindowsDriver(URL("http://127.0.0.1:4723"), capabilities)
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

        println("PowerShell output: $output")

        if (output.contains("NOT_FOUND")) return null

        val hwndLine = output.lines()
            .map { it.trim() }
            .lastOrNull { it.matches(Regex("[0-9A-Fa-f]+")) }
            ?: return null

        val decimal = hwndLine.toLong(16)
        return if (decimal == 0L) null else hwndLine
    }
    fun quitDriver() {
        driver?.quit()
        driver = null
    }

    private fun startWinAppDriverIfNeeded() {
        if (!isWinAppDriverRunning()) {
            println("⏳ Starting WinAppDriver...")

            val processBuilder = ProcessBuilder(
                "C:\\Program Files (x86)\\Windows Application Driver\\WinAppDriver.exe"
            )
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD)
            processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD)
            processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE)

            winAppDriverProcess = processBuilder.start()

            val timeoutMs = 5000
            val startTime = System.currentTimeMillis()
            while (!isWinAppDriverRunning()) {
                if (System.currentTimeMillis() - startTime > timeoutMs) {
                    throw RuntimeException("❌ WinAppDriver did not start in time")
                }
                Thread.sleep(500)
            }
            println("✅ WinAppDriver started successfully")
        } else {
            println("✅ WinAppDriver already running.")
        }
    }

    private fun isWinAppDriverRunning(): Boolean {
        return try {
            val url = URL("http://127.0.0.1:4723/status")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 1000
            conn.responseCode == 200
        } catch (e: Exception) {
            false
        }
    }
}