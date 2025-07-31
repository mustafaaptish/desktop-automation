package insight.steps

import insight.drivers.DesktopDriver
import insight.sessions.DesktopSession
import insight.sessions.SessionUtils
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.WebElement

/**
 * Base class for step classes. Provides access to the desktop session and driver.
 */
abstract class DesktopBaseStep {

    companion object {

        fun session(): DesktopSession {
            if (!isSessionSet()) {
                setSession()
            }
            return getSession()
        }

        private fun isSessionSet(): Boolean {
            return SessionUtils.get(DesktopSession::class.java) != null
        }

        private fun setSession() {
            SessionUtils.put(DesktopSession::class.java, DesktopSession())
        }

        private fun getSession(): DesktopSession {
            return SessionUtils.get(DesktopSession::class.java)!!
        }

        fun getDriver(): WindowsDriver<WebElement> {
            return DesktopDriver.getDriver()
        }

        fun clearSession() {
            DesktopDriver.quitDriver()
            SessionUtils.remove(DesktopSession::class.java)
        }
    }
}