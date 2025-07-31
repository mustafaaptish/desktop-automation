package insight.pages

import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class ClinkNetworkSettingsPage (val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    private val networkSettingsText = By.name("Network Settings")
    //    private val networkSettingsButton = driver.findElementByName("1. Network Settings")
    private val networkSettingsButton: WebElement
        get() = driver.findElementByName("1. Network Settings")

    private val accessPointNameInput: WebElement
        get() = driver.findElementByAccessibilityId("accessPoint_name")

    private val networkSettingsUsernameInput: WebElement
        get() = driver.findElementByAccessibilityId("accessPoint_user")

    private val networkSettingsPasswordInput: WebElement
        get() = driver.findElementByAccessibilityId("accessPoint_password")

    private val saveButton: WebElement
        get() = driver.findElementByName("Save")

    private val cancelButton: WebElement
        get() = driver.findElementByName("Cancel")

    private val dashBoardTab: WebElement
        get() = driver.findElementByAccessibilityId("DeviceTabDashboard")


    fun getAccessPointName(): String {
        return getTextFromInput(accessPointNameInput)
    }

    fun getAccessPointUsername(): String {
        return getTextFromInput(networkSettingsUsernameInput)
    }

    fun getAccessPointPassword(): String {
        return getTextFromInput(networkSettingsPasswordInput)
    }

    fun clickOnDashboardTab() : ClinkNetworkSettingsPage {
        dashBoardTab.click()
        println("Clicked on 'Dashboard' tab on bav bar")
        return this
    }

    fun fillAccessPointName(text: String) : ClinkNetworkSettingsPage {
        clearAndEnterText(accessPointNameInput, text)
        println("Entered Access Point Name: $text")
        return this
    }

    fun fillAccessPointUserName(text: String) : ClinkNetworkSettingsPage {
        clearAndEnterText(networkSettingsUsernameInput,text)
        println("Entered Access Point Username: $text")
        return this
    }

    fun fillAccessPointPassword(text: String) : ClinkNetworkSettingsPage {
        clearAndEnterText(networkSettingsPasswordInput, text)
        println("Entered Access Point Password: $text")
        return this
    }

    fun clickSave(): ClinkNetworkSettingsPage {
        saveButton.click()
        println("Clicked on 'Save' button")
        return this
    }

    fun clickCancel(): ClinkNetworkSettingsPage {
        cancelButton.click()
        println("Clicked on 'Cancel' button")
        return this
    }


    fun waitForNetworkSettingsToBeVisible(timeoutSeconds: Long = 30): Boolean {
        val wait = WebDriverWait(driver, timeoutSeconds)
        return try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(networkSettingsText))
            println("Network Settings is visible")
            true
        } catch (e: Exception) {
            println("Network Settings not visible within $timeoutSeconds seconds")
            false
        }
    }

    fun clickOnNetworkSettingsButton() {
        networkSettingsButton.click()
        println("Clicked on 'Network Settings' button")
    }


}