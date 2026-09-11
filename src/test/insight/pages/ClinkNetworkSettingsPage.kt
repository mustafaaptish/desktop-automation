package insight.pages

import insight.utilities.log.logInfo
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class ClinkNetworkSettingsPage(private val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    private val dashboardTab: By = byAccessibilityId("DeviceTabDashboard")
    private val networkSettingsTab: By = byAccessibilityId("DeviceTabNetworkSettings")
    private val surveySettingsTab: By = byAccessibilityId("DeviceTabSurveySettings")
    private val advancedTab: By = byAccessibilityId("DeviceTabAdvanced")
    private val laneSetupTab: By = byAccessibilityId("DeviceTabLanesSetup")
    private val disconnectDeviceButton: By = byAccessibilityId("disconnectBtn")

    private val networkSettingsText: By = By.name("Network Settings")
    private val networkSettingsButton: By = By.name("1. Network Settings")
    private val surveySettingsButton: By = By.name("2. Survey Settings")
    private val advancedButton: By = byAccessibilityId("AdvancedButton")

    private val accessPointNameInput: By = byAccessibilityId("accessPoint_name")
    private val accessPointUsernameInput: By = byAccessibilityId("accessPoint_user")
    private val accessPointPasswordInput: By = byAccessibilityId("accessPoint_password")

    fun clickOnDashboardTab(): ClinkNetworkSettingsPage {
        click(dashboardTab)
        logInfo("Clicked on 'Dashboard' tab on nav bar")
        return this
    }

    fun clickOnNetworkSettingsTab(): ClinkNetworkSettingsPage {
        click(networkSettingsTab)
        logInfo("Clicked on 'Network Settings' tab")
        return this
    }

    fun clickOnSurveySettingsTab(): ClinkNetworkSettingsPage {
        click(surveySettingsTab)
        logInfo("Clicked on 'Survey Settings' tab")
        return this
    }

    fun clickOnAdvancedTab(): ClinkNetworkSettingsPage {
        click(advancedTab)
        logInfo("Clicked on 'Advanced' tab")
        return this
    }

    fun clickOnLaneSetupTab(): ClinkNetworkSettingsPage {
        click(laneSetupTab)
        logInfo("Clicked on 'Lane(s) Setup' tab")
        return this
    }

    fun clickOnAdvancedButton(): ClinkNetworkSettingsPage {
        click(advancedButton)
        logInfo("Clicked on 'Advanced' button")
        return this
    }

    fun clickOnDisconnectDeviceButton(): ClinkNetworkSettingsPage {
        click(disconnectDeviceButton)
        logInfo("Clicked on 'Disconnect Device' button")
        return this
    }

    fun clickOnNetworkSettingsButton(): ClinkNetworkSettingsPage {
        click(networkSettingsButton)
        logInfo("Clicked on 'Network Settings' button")
        return this
    }

    fun clickOnSurveySettingsButton(): ClinkNetworkSettingsPage {
        click(surveySettingsButton)
        logInfo("Clicked on 'Survey Settings' button")
        return this
    }

    fun getAccessPointName(): String = getInputValue(accessPointNameInput)

    fun getAccessPointUsername(): String = getInputValue(accessPointUsernameInput)

    fun getAccessPointPassword(): String = getInputValue(accessPointPasswordInput)

    fun fillAccessPointName(text: String): ClinkNetworkSettingsPage {
        enterText(accessPointNameInput, text)
        logInfo("Entered Access Point Name: $text")
        return this
    }

    fun fillAccessPointUserName(text: String): ClinkNetworkSettingsPage {
        enterText(accessPointUsernameInput, text)
        logInfo("Entered Access Point Username: $text")
        return this
    }

    fun fillAccessPointPassword(text: String): ClinkNetworkSettingsPage {
        enterText(accessPointPasswordInput, text)
        logInfo("Entered Access Point Password: $text")
        return this
    }

    fun clickSave(): ClinkNetworkSettingsPage {
        click(GlobalLocators.SAVE_BUTTON)
        logInfo("Clicked on 'Save' button")
        return this
    }

    fun clickCancel(): ClinkNetworkSettingsPage {
        click(GlobalLocators.CANCEL_BUTTON)
        logInfo("Clicked on 'Cancel' button")
        return this
    }

    fun isLabelPresent(labelText: String): Boolean {
        val present = isElementImmediatelyDisplayed(By.name(labelText))
        logInfo("Label '$labelText' is ${if (present) "present" else "NOT present"}")
        return present
    }

    fun waitForNetworkSettingsToBeVisible(timeoutSeconds: Long = DEFAULT_WAIT_TIME): Boolean {
        val visible = isElementDisplayed(networkSettingsText, timeoutSeconds)
        logInfo(if (visible) "Network Settings is visible" else "Network Settings not visible within $timeoutSeconds seconds")
        return visible
    }
}
