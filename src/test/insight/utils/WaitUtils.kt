package insight.utils

import insight.drivers.DesktopDriver
import insight.pages.BasePage.Companion.DEFAULT_POLLING_FREQUENCY
import insight.pages.BasePage.Companion.DEFAULT_WAIT_TIME
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

/**
 * Centralized wait helpers. All waits resolve the thread-local driver internally,
 * so callers never pass a driver around.
 */
object WaitUtils {

    private val driver get() = DesktopDriver.getMyDriver()

    fun waitFor(seconds: Long) = Thread.sleep(seconds * 1000)

    fun waitForMilliSec(milliseconds: Long) = Thread.sleep(milliseconds)

    fun waitForVisibility(locator: By, timeoutInSeconds: Long = DEFAULT_WAIT_TIME): WebElement =
        newWait(timeoutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator))

    fun waitForPresence(locator: By, timeoutInSeconds: Long = DEFAULT_WAIT_TIME): WebElement =
        newWait(timeoutInSeconds).until(ExpectedConditions.presenceOfElementLocated(locator))

    fun waitForInvisibility(locator: By, timeoutInSeconds: Long = DEFAULT_WAIT_TIME): Boolean =
        newWait(timeoutInSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator))

    fun waitForClickAbility(locator: By, timeoutInSeconds: Long = DEFAULT_WAIT_TIME): WebElement =
        newWait(timeoutInSeconds).until(ExpectedConditions.elementToBeClickable(locator))

    fun waitForClickAbility(element: WebElement, timeoutInSeconds: Long = DEFAULT_WAIT_TIME): WebElement =
        newWait(timeoutInSeconds).until(ExpectedConditions.elementToBeClickable(element))

    private fun newWait(timeoutInSeconds: Long): WebDriverWait =
        WebDriverWait(driver, timeoutInSeconds, DEFAULT_POLLING_FREQUENCY)
}
