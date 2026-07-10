package insight.pages

import insight.utilities.log.logInfo
import insight.utils.WaitUtils
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

class ClinkLoginPage(private val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    private val connectToDeviceButton: By = byAccessibilityId("ConnectDeviceButton")
    private val usernameField: By = byAccessibilityId("txtboxUsername")
    private val passwordField: By = byAccessibilityId("txtboxPassword")
    private val loginButton: By = byAccessibilityId("AuthLoginButton")
    private val devicesDropdown: By = byAccessibilityId("comboDevices")
    private val selectButton: By = byAccessibilityId("SelectDeviceButton")
    private val insightTokenRadioButton: By = byAccessibilityId("ConnectionTypeToken")
    private val tokenInputField: By = byAccessibilityId("txtboxToken")
    private val loginWithTokenButton: By = byAccessibilityId("AuthTokenButton")

    fun clickConnectToDevice(): ClinkLoginPage {
        click(connectToDeviceButton)
        logInfo("Clicked on 'Connect to a Device' button")
        return this
    }

    fun clickLogin(): ClinkLoginPage {
        click(loginButton)
        logInfo("Clicked on 'Log In' button")
        return this
    }

    fun clickLoginWithTokenButton(): ClinkLoginPage {
        click(loginWithTokenButton)
        logInfo("Clicked 'Login' button")
        return this
    }

    fun enterToken(token: String): ClinkLoginPage {
        enterText(tokenInputField, token)
        return this
    }

    fun selectInsightTokenRadioButton(): ClinkLoginPage {
        click(insightTokenRadioButton)
        logInfo("Selected 'Insight Token' as connection method")
        return this
    }

    fun clickOnDevicesDropdown(): ClinkLoginPage {
        click(devicesDropdown)
        logInfo("Clicked on Devices dropdown")
        return this
    }

    fun enterUsername(text: String): ClinkLoginPage {
        findElement(usernameField).sendKeys(text)
        logInfo("Entered username: '$text'")
        return this
    }

    fun enterPassword(text: String): ClinkLoginPage {
        findElement(passwordField).sendKeys(text)
        logInfo("Entered password")
        return this
    }

    fun selectDeviceFromDropdown(deviceName: String): ClinkLoginPage {
        WaitUtils.waitForMilliSec(500)
        val dropdown = findElement(devicesDropdown)
        dropdown.sendKeys(deviceName)
        dropdown.sendKeys(Keys.ENTER)
        logInfo("Selected device: '$deviceName'")
        return this
    }

    fun clickOnSelectButton(): ClinkLoginPage {
        click(selectButton)
        logInfo("Clicked on 'Select' button")
        return this
    }

    fun closeApp() {
        driver.close()
        logInfo("Closed the application")
    }
}
