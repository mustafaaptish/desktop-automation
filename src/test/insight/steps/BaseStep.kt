package insight.steps

import insight.dependencies.Main
import insight.drivers.DesktopDriver
import insight.utils.SessionUtils
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.WebElement

/**
 * Base class for step classes. Provides access to the per-scenario session and the
 * thread-local driver. Pages are built on demand in step classes via lazy getters.
 */
abstract class BaseStep {

    protected val driver: WindowsDriver<WebElement>
        get() = DesktopDriver.getMyDriver()

    companion object {

        fun session(): Main {
            if (!isSessionSet()) {
                setSession()
            }
            return getSession()
        }

        private fun isSessionSet(): Boolean = SessionUtils.get(Main::class.java) != null

        private fun setSession() = SessionUtils.put(Main::class.java, Main())

        private fun getSession(): Main = SessionUtils.get(Main::class.java)!!

        fun clearSession() = SessionUtils.remove(Main::class.java)
    }
}
