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
        "clink" to "C:\\Program Files (x86)\\Clearview Intelligence\\CLink\\CLink.exe",
        "ms teams" to "C:\\Program Files\\WindowsApps\\MSTeams_25185.410.3812.8024_x64__8wekyb3d8bbwe\\ms-teams.exe"
    )

    fun getDriver(): WindowsDriver<WebElement> {
        return driver ?: throw IllegalStateException("Desktop driver not initialized!")
    }

    fun startApp(appName: String): WindowsDriver<WebElement> {
        if (driver == null) {
            startWinAppDriverIfNeeded()

            val path = appPaths[appName.lowercase(Locale.getDefault())]
                ?: throw IllegalArgumentException("App '$appName' not configured")

            val capabilities = DesiredCapabilities()
            capabilities.setCapability("app", path)
            capabilities.setCapability("platformName", "Windows")
            capabilities.setCapability("deviceName", "WindowsPC")

            val driverUrl = URL("http://127.0.0.1:4723")
            driver = WindowsDriver(driverUrl, capabilities)
            driver!!.manage().window().maximize()
            println("✅ Launched and maximized: $appName")
        }
        return driver!!
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

            // ✅ Wait for it to be fully ready (max 5 seconds)
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
