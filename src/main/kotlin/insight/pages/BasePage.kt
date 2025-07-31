package insight.pages

import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

/**
 * Base class for WinAppDriver-based page objects.
 * Provides common utility methods to interact with desktop elements.
 */
open class BasePage(private val driver: WindowsDriver<WebElement>) {

    fun clearAndEnterText(locator: By, text: String) {
        val element = findElement(locator)
        element.sendKeys(Keys.CONTROL, "a")
        element.sendKeys(Keys.DELETE)
        element.sendKeys(text)
        println("Cleared existing text and entered: '$text'")
    }

    fun clearAndEnterText(element: WebElement, text: String) {
        element.sendKeys(Keys.CONTROL, "a")
        element.sendKeys(Keys.DELETE)
        element.sendKeys(text)
        println("Entered: '$text'")
    }

     fun getTextFromInput(element: WebElement): String {
        return element.getAttribute("Value.Value")
    }

    /**
     * Finds a WebElement using the given locator.
     */
    fun findElement(locator: By): WebElement {
        return driver.findElement(locator)
    }

    /**
     * Enters text into the specified element.
     */
    fun enterText(locator: By, text: String) {
        val element = findElement(locator)
        element.clear()
        element.sendKeys(text)
    }

    /**
     * Retrieves the text content of the specified element.
     */
    fun getText(locator: By): String {
        return findElement(locator).text
    }

    /**
     * Clicks the specified element.
     */
    fun click(locator: By) {
        findElement(locator).click()
    }
}