package insight.pages

import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait

class MsTeamsPage(private val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    private val myChatItem: WebElement
        get() = driver.findElements(By.xpath("//*[contains(@Name, 'Chat Mustafa')]"))
            .firstOrNull { it.getAttribute("ControlType")?.contains("TreeItem") == true }
            ?: throw NoSuchElementException("Could not find own chat item in Teams sidebar")

    private val messageInputBox: WebElement
        get() = waitForMessageInput()

    private val sendButton: WebElement
        get() = driver.findElement(By.xpath("//*[@Name='Send (Ctrl+Enter)']"))

    fun clickOnMyChat(): MsTeamsPage {
        myChatItem.click()
        println("Clicked on own chat (Mustafa)")
        waitForMessageInput() // wait for chat pane to fully load before returning
        return this
    }

    fun typeMessage(message: String): MsTeamsPage {
        val input = messageInputBox
        input.click()
        input.sendKeys(message)
        println("Typed message: '$message'")
        return this
    }

    fun sendMessage(): MsTeamsPage {
        sendButton.click()
        println("Sent message")
        Thread.sleep(500) // brief pause to allow Teams to process send before next iteration
        return this
    }

    fun clearMessage(): MsTeamsPage {
        val input = messageInputBox
        input.click()
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"))
        input.sendKeys(Keys.DELETE)
        println("Cleared message input")
        return this
    }

    private fun waitForMessageInput(): WebElement {
        val timeoutSeconds = 15L
        val pollMs = 500L
        val deadline = System.currentTimeMillis() + (timeoutSeconds * 1000)

        while (System.currentTimeMillis() < deadline) {
            // Try Name only first - most reliable in WinAppDriver for WebView2 content
            val byName = driver.findElements(By.xpath("//*[@Name='Type a message']"))
                .firstOrNull {
                    runCatching { it.isEnabled && it.isDisplayed }.getOrDefault(false)
                }
            if (byName != null) return byName

            // Fallback: match by AutomationId prefix which is stable across the UUID suffix
            val byId = driver.findElements(By.xpath("//*[starts-with(@AutomationId, 'new-message-')]"))
                .firstOrNull {
                    runCatching { it.isEnabled && it.isDisplayed }.getOrDefault(false)
                }
            if (byId != null) return byId

            Thread.sleep(pollMs)
        }

        throw NoSuchElementException("Message input box did not appear within ${timeoutSeconds}s")
    }
}