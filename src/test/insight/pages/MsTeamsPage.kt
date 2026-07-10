package insight.pages

import insight.utilities.log.logInfo
import insight.utils.WaitUtils
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

class MsTeamsPage(private val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    private val sendButton: By = By.xpath("//*[@Name='Send (Ctrl+Enter)']")
    private val messageInputByName: By = By.xpath("//*[@Name='Type a message']")
    // AutomationId prefix is stable across the UUID suffix
    private val messageInputById: By = By.xpath("//*[starts-with(@AutomationId, 'new-message-')]")

    private fun findMyChatItem(): WebElement? =
        findElements(By.xpath("//*[contains(@Name, 'Chat Mustafa')]"))
            .firstOrNull { it.getAttribute("ControlType")?.contains("TreeItem") == true }

    // Teams can take a long time to render the sidebar after launch, so poll instead of a one-shot lookup
    private fun waitForMyChatItem(timeoutSeconds: Long = 60): WebElement {
        val pollMs = 1000L
        val deadline = System.currentTimeMillis() + (timeoutSeconds * 1000)

        while (System.currentTimeMillis() < deadline) {
            val chatItem = runCatching { findMyChatItem() }.getOrNull()
            if (chatItem != null) return chatItem
            WaitUtils.waitForMilliSec(pollMs)
        }

        throw NoSuchElementException("Could not find own chat item in Teams sidebar within ${timeoutSeconds}s")
    }

    fun clickOnMyChat(): MsTeamsPage {
        waitForMyChatItem().click()
        logInfo("Clicked on own chat")
        waitForMessageInput() // wait for chat pane to fully load before returning
        return this
    }

    fun typeMessage(message: String): MsTeamsPage {
        val input = waitForMessageInput()
        input.click()
        input.sendKeys(message)
        logInfo("Typed message: '$message'")
        return this
    }

    fun sendMessage(): MsTeamsPage {
        click(sendButton)
        logInfo("Sent message")
        WaitUtils.waitForMilliSec(500) // brief pause to allow Teams to process send before next iteration
        return this
    }

    fun clearMessage(): MsTeamsPage {
        val input = waitForMessageInput()
        input.click()
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"))
        input.sendKeys(Keys.DELETE)
        logInfo("Cleared message input")
        return this
    }

    private fun waitForMessageInput(): WebElement {
        val timeoutSeconds = 15L
        val pollMs = 500L
        val deadline = System.currentTimeMillis() + (timeoutSeconds * 1000)

        while (System.currentTimeMillis() < deadline) {
            // Try Name only first - most reliable in WinAppDriver for WebView2 content
            val byName = findElements(messageInputByName)
                .firstOrNull { runCatching { it.isEnabled && it.isDisplayed }.getOrDefault(false) }
            if (byName != null) return byName

            val byId = findElements(messageInputById)
                .firstOrNull { runCatching { it.isEnabled && it.isDisplayed }.getOrDefault(false) }
            if (byId != null) return byId

            WaitUtils.waitForMilliSec(pollMs)
        }

        throw NoSuchElementException("Message input box did not appear within ${timeoutSeconds}s")
    }
}
