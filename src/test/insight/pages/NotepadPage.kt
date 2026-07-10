package insight.pages

import insight.utilities.log.logInfo
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class NotepadPage(private val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    private val editArea: By = By.className("RichEditD2DPT")
    private val addNewTabButton: By = By.name("Add New Tab")

    fun enterText(text: String): NotepadPage {
        findElement(editArea).sendKeys(text)
        logInfo("Entered text: '$text'")
        return this
    }

    fun getText(): String = getInputValue(editArea)

    fun clickOnDoNotSave(): NotepadPage {
        click(GlobalLocators.DONT_SAVE_BUTTON, timeoutInSeconds = 5)
        logInfo("Clicked on 'Don't Save' button")
        return this
    }

    fun clickOnAddNewTab(): NotepadPage {
        click(addNewTabButton)
        logInfo("Clicked on 'Add New Tab' button")
        return this
    }

    fun clearText(): NotepadPage {
        clearText(editArea)
        logInfo("Cleared text from edit area")
        return this
    }

    fun closeNotepad() {
        driver.close()
        try {
            clickOnDoNotSave()
        } catch (e: Exception) {
            logInfo("No 'Do Not Save' dialog appeared or button not found: ${e.message}")
        }
    }
}
