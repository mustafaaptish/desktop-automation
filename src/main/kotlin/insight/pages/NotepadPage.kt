package insight.pages

import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

class NotepadPage(private val driver: WindowsDriver<WebElement>): BasePage(driver) {

    private val editArea: WebElement
        get() = driver.findElement(By.className("RichEditD2DPT"))

    private val doNotSaveButton: WebElement
        get() = driver.findElement(By.name("Don't save"))

    private val addNewTabButton: WebElement
    get() = driver.findElementByName("Add New Tab")

    fun enterText(text: String) {
        editArea.sendKeys(text)
        println("Entered text: '$text'")
    }

    fun getText(): String {
        return getTextFromInput(editArea)
    }

    fun clickOnDoNotSave() {
        doNotSaveButton.click()
        println("Clicked on 'Don't Save' button")
    }

    fun clickOnAddNewTab() {
        addNewTabButton.click()
        println("Clicked on 'Add New tab' button")
    }

     fun clearText() {
         editArea.sendKeys(Keys.CONTROL,"a")
         editArea.sendKeys(Keys.DELETE)
         println("Cleared text from edit area")
     }

    fun closeNotepad() {
        driver.close()

        try {
            clickOnDoNotSave()
        } catch (e: Exception) {
            println("No 'Do Not Save' dialog appeared or button not found: ${e.message}")
        }
    }

//    fun closeTab() {
//        closeButton.click()
//    }
//
//    fun closeAllTabs() {
//        // Find all close buttons by AutomationId "CloseButton"
//        val closeButtons = driver.findElementsByAccessibilityId("CloseButton")
//
//        for (button in closeButtons) {
//            button.click()
//
//            // Wait briefly if needed for the dialog to appear
//            Thread.sleep(500)
//
//            // Handle "Don't Save" dialog if it appears
//            try {
//                clickOnDoNotSave()
//            } catch (e: Exception) {
//                println("No 'Do Not Save' dialog after closing a tab: ${e.message}")
//            }
//        }
//    }

}
