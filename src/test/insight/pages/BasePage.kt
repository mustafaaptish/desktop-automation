package insight.pages

import insight.utilities.log.logInfo
import insight.utils.DriverUtils
import insight.utils.WaitUtils
import io.appium.java_client.MobileBy
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

/**
 * Base class for WinAppDriver-based page objects.
 * Provides fluent, wait-backed helpers to interact with desktop elements.
 */
open class BasePage(private val driver: WindowsDriver<WebElement>) {

    companion object {
        const val DEFAULT_WAIT_TIME = 30L
        const val DEFAULT_POLLING_FREQUENCY = 500L
    }

    // ---------- Locator factories ----------

    protected fun byAccessibilityId(id: String): By = MobileBy.AccessibilityId(id)

    // ---------- Element lookup ----------

    fun findElement(locator: By): WebElement = driver.findElement(locator)

    fun findElements(locator: By): List<WebElement> = driver.findElements(locator)

    // ---------- Clicks ----------

    fun click(locator: By, timeoutInSeconds: Long = DEFAULT_WAIT_TIME): BasePage {
        DriverUtils.retryOnStaleElement {
            WaitUtils.waitForClickAbility(locator, timeoutInSeconds).click()
        }
        logInfo("Clicked element: $locator")
        return this
    }

    fun click(element: WebElement): BasePage {
        DriverUtils.retryOnStaleElement { element.click() }
        return this
    }

    // ---------- Text entry ----------

    fun enterText(locator: By, text: String): BasePage {
        val element = WaitUtils.waitForVisibility(locator)
        clearAndEnterText(element, text)
        return this
    }

    fun clearAndEnterText(locator: By, text: String): BasePage {
        return enterText(locator, text)
    }

    fun clearAndEnterText(element: WebElement, text: String): BasePage {
        element.sendKeys(Keys.CONTROL, "a")
        element.sendKeys(Keys.DELETE)
        element.sendKeys(text)
        logInfo("Cleared existing text and entered: '$text'")
        return this
    }

    fun clearText(locator: By): BasePage {
        val element = findElement(locator)
        element.sendKeys(Keys.CONTROL, "a")
        element.sendKeys(Keys.DELETE)
        return this
    }

    // ---------- Reads ----------

    fun getText(locator: By): String = findElement(locator).text

    fun getInputValue(locator: By): String = getTextFromInput(findElement(locator))

    fun getTextFromInput(element: WebElement): String = element.getAttribute("Value.Value")

    // ---------- Visibility ----------

    fun isElementDisplayed(locator: By, timeoutInSeconds: Long = DEFAULT_WAIT_TIME): Boolean {
        return try {
            WaitUtils.waitForVisibility(locator, timeoutInSeconds)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isElementImmediatelyDisplayed(locator: By): Boolean {
        return try {
            findElement(locator).isDisplayed
        } catch (e: Exception) {
            false
        }
    }
}
