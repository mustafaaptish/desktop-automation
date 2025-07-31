package insight.pages

import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

class ClinkLoginPage(private val driver: WindowsDriver<WebElement>): BasePage(driver) {

    private val connectToDeviceButton: WebElement
        get() = driver.findElementByAccessibilityId("ConnectDeviceButton")

    private val usernameField: WebElement
        get() = driver.findElementByAccessibilityId("txtboxUsername")

    private val passwordField: WebElement
        get() = driver.findElementByAccessibilityId("txtboxPassword")

    private val loginButton: WebElement
        get() = driver.findElementByAccessibilityId("AuthLoginButton")

    private val devicesDropdown: WebElement
        get() = driver.findElementByAccessibilityId("comboDevices")

    private val selectButton: WebElement
        get() = driver.findElementByAccessibilityId("SelectDeviceButton")

    private val insightTokenRadioButton: WebElement
        get() = driver.findElementByAccessibilityId("ConnectionTypeToken")

    private val tokenInputField: WebElement
        get() = driver.findElementByAccessibilityId("txtboxToken")

    private val loginWithTokenButton: WebElement
        get() = driver.findElementByAccessibilityId("AuthTokenButton")


    fun clickLoginWithTokenButton(): ClinkLoginPage {
        loginWithTokenButton.click()
        println("Clicked 'Login' button")
        return this
    }

    fun enterToken(token: String): ClinkLoginPage {
        clearAndEnterText(tokenInputField, token)
        return this

    }
    fun selectInsightTokenRadioButton() : ClinkLoginPage {
        insightTokenRadioButton.click()
        println("Selected 'Insight Token' as connection method")
        return this
    }

    fun clickOnDevicesDropdown() : ClinkLoginPage {
        devicesDropdown.click()
        println("Clicked on Devices dropdown")
        return this
    }

    fun clickConnectToDevice() {
        connectToDeviceButton.click()
        println("Clicked on 'Connect to a Device' button")
    }

    fun clickLogin() : ClinkLoginPage {
        loginButton.click()
        println("Clicked on ''Log In' button")
        return this
    }

    fun enterUsername(text: String) : ClinkLoginPage{
        usernameField.sendKeys(text)
        println("Entered text: '$text'")
        return this
    }

    fun enterPassword(text: String) : ClinkLoginPage {
        passwordField.sendKeys(text)
        println("Entered text: '$text'")
        return this
    }

    fun selectDeviceFromDropdown(deviceName: String): ClinkLoginPage {
        Thread.sleep(500)
        devicesDropdown.sendKeys(deviceName)
        devicesDropdown.sendKeys(Keys.ENTER)
        println("Selected device: '$deviceName'")
        return this
    }

    fun clickOnSelectButton(): ClinkLoginPage{
        selectButton.click()
        println("Clicked on 'Select' button")
        return this
    }

    fun closeApp() {
        driver.close()
        println("Closed the application")
    }

}