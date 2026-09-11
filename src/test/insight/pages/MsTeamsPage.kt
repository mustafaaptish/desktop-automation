package insight.pages

import insight.utilities.log.logInfo
import insight.utils.WaitUtils
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriverException

class MsTeamsPage(private val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    companion object {
        // Resolving the message box is a full-tree XPath walk over Teams' WebView2 content and is
        // expensive, so the element is cached across page-object instances and only re-resolved
        // when it goes stale.
        private var cachedMessageInput: WebElement? = null

        fun resetCache() {
            cachedMessageInput = null
        }
    }

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
        resetCache() // the previous chat's input box is gone once we switch chats
        waitForMyChatItem().click()
        logInfo("Clicked on own chat")
        focusMessageInput() // wait for chat pane to load and put the caret in the box
        return this
    }

    /**
     * Resolves the message box, clicks it once to give it focus and caches it.
     * Subsequent calls reuse the cached element, so no further tree walks or clicks are needed.
     */
    fun focusMessageInput(): WebElement {
        val input = messageInput()
        input.click()
        return input
    }

    fun typeMessage(message: String): MsTeamsPage {
        withInput { it.sendKeys(message.singleLine()) }
        logInfo("Typed message: '$message'")
        return this
    }

    /**
     * Types the message and clears it again in two driver round trips - the fast path used by the
     * long-running "type random text for N minutes" step.
     */
    fun typeAndClearMessage(message: String): MsTeamsPage {
        withInput {
            // Clear any existing text first
            it.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE)

            // Type the message
            it.sendKeys(message.singleLine())

            // Wait 2 seconds before clearing it
            Thread.sleep(1500)

            // Select all and delete
            it.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE)
        }
        return this
    }

    fun sendMessage(): MsTeamsPage {
        click(sendButton)
        logInfo("Sent message")
        return this
    }

    fun clearMessage(): MsTeamsPage {
        withInput { it.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE) }
        logInfo("Cleared message input")
        return this
    }

    /**
     * Runs [action] against the cached input, re-resolving and retrying once if the cached element
     * has gone stale (Teams re-renders the composer on its own from time to time).
     */
    private fun <T> withInput(action: (WebElement) -> T): T =
        try {
            action(messageInput())
        } catch (e: WebDriverException) {
            resetCache()
            action(focusMessageInput())
        }

    private fun messageInput(): WebElement = cachedMessageInput ?: waitForMessageInput().also {
        cachedMessageInput = it
    }

    private fun waitForMessageInput(): WebElement {
        val timeoutSeconds = 15L
        val pollMs = 500L
        val deadline = System.currentTimeMillis() + (timeoutSeconds * 1000)

        while (System.currentTimeMillis() < deadline) {
            // Try Name only first - most reliable in WinAppDriver for WebView2 content
            val byName = findElements(messageInputByName).firstOrNull { it.isInteractive() }
            if (byName != null) return byName

            val byId = findElements(messageInputById).firstOrNull { it.isInteractive() }
            if (byId != null) return byId

            WaitUtils.waitForMilliSec(pollMs)
        }

        throw NoSuchElementException("Message input box did not appear within ${timeoutSeconds}s")
    }

    /** A newline in the composer sends the message and a tab moves focus, so neither is ever typed. */
    private fun String.singleLine(): String = replace(Regex("\\s+"), " ").trim()

    private fun WebElement.isInteractive(): Boolean =
        runCatching { isEnabled && isDisplayed }.getOrDefault(false)
}
