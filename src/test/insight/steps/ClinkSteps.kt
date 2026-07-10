package insight.steps

import insight.pages.ClinkAdvancedPage
import insight.pages.ClinkDashboardPage
import insight.pages.ClinkLoginPage
import insight.pages.ClinkNetworkSettingsPage
import insight.pages.ClinkSurveySettingsPage
import insight.utilities.AppConfig
import insight.utilities.log.logInfo
import insight.utilities.property.AutomationUtils
import insight.utilities.props.PropertyFactory
import insight.utils.DesktopUtils
import insight.utils.FileUtils
import insight.utils.TokenUtils
import insight.utils.WaitUtils
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert

class ClinkSteps : BaseStep() {

    private val loginPage get() = ClinkLoginPage(driver)
    private val dashboardPage get() = ClinkDashboardPage(driver)
    private val networkSettingsPage get() = ClinkNetworkSettingsPage(driver)
    private val surveySettingsPage get() = ClinkSurveySettingsPage(driver)
    private val advancedPage get() = ClinkAdvancedPage(driver)

    private val environmentConfig = PropertyFactory.environmentProperty()

    @Then("I click connect to a device$")
    fun `I click connect to a device`() {
        loginPage.clickConnectToDevice()
    }

    @Then("I enter my Clink credentials and login")
    fun `I enter my Clink credentials and login`() {
        loginPage
            .enterUsername(AutomationUtils.getUsername())
            .enterPassword(AutomationUtils.getPassword())
            .clickLogin()
    }

    @When("I enter my Insight Token and login")
    fun `I enter my Insight Token and login`() {
        val token = TokenUtils.systemAdminToken().token

        loginPage
            .selectInsightTokenRadioButton()
            .enterToken(token)
            .clickLoginWithTokenButton()
    }

    @Then("I select a device from the dropdown")
    fun `I select a device from the dropdown`() {
        val deviceEntry = "${environmentConfig.deviceName()} (${environmentConfig.deviceId()})"
        loginPage
            .clickOnDevicesDropdown()
            .selectDeviceFromDropdown(deviceEntry)
            .clickOnSelectButton()
    }

    @Then("I should see the (.*) screen$")
    fun `I should see the screen`(screenName: String) {
        val isVisible = when (screenName.lowercase()) {
            "dashboard" -> dashboardPage.waitForDashboardVisible()
            "network settings" -> networkSettingsPage.waitForNetworkSettingsToBeVisible()
            "survey settings" -> surveySettingsPage.waitForSurveySettingsToBeVisible()
            else -> throw IllegalArgumentException("No visibility check defined for screen: $screenName")
        }

        Assert.assertTrue("$screenName screen is not visible", isVisible)
    }

    @Then("I verify Network Settings labels are present")
    fun `I verify Network Settings labels are present`() {
        val page = networkSettingsPage
        val expectedLabels = listOf(
            "Access Point Name",
            "Username",
            "Password",
            "Radio Access Technology",
            "Ethernet Information",
            "Req",
            "PHY Address",
            "MAC Address",
            "IP Address",
            "Gateway",
            "Subnet Mask",
            "DNS",
            "DHCP Lease(s)",
            "Sockets"
        )
        expectedLabels.forEach { label ->
            Assert.assertTrue("Label '$label' not found", page.isLabelPresent(label))
        }
    }

    @Then("I should see correct details")
    fun `I should see correct details`() {
        val page = dashboardPage

        Assert.assertTrue("Dashboard not visible", page.waitForDashboardVisible())
        Assert.assertTrue("Overview section missing", page.verifyOverviewSection())
        Assert.assertTrue("Insight ID missing or incorrect", page.verifyInsightID(environmentConfig.deviceId()))
        Assert.assertTrue("Insight Name missing or incorrect", page.verifyInsightName(environmentConfig.deviceName()))
        Assert.assertTrue("External ID missing or incorrect", page.verifyExternalID(environmentConfig.deviceExternalId()))
        Assert.assertTrue("Application version missing or incorrect", page.verifyApplicationVersion(environmentConfig.deviceApplicationVersion()))
        Assert.assertTrue("Boot Version missing or incorrect", page.verifyBootVersion(environmentConfig.deviceBootVersion()))
        Assert.assertTrue("Unit Up Time missing or incorrect", page.verifyUnitUpTime())

        Assert.assertTrue("Current Power State missing or incorrect", page.verifyCurrentPowerStatePresent())
        Assert.assertTrue("Current Connection State missing or incorrect", page.verifyCurrentConnectionStatePresent())
        Assert.assertTrue("Current Faults missing or incorrect", page.verifyCurrentFaults(""))
        Assert.assertTrue("Power Status section missing", page.verifyPowerStatusSectionIsPresent())
        Assert.assertTrue("Power Mode label missing", page.verifyPowerModePresent())
        Assert.assertTrue("External Voltage label missing", page.verifyExternalVoltagePresent())
        Assert.assertTrue("Battery Voltage label missing", page.verifyBatteryVoltagePresent())
        Assert.assertTrue("Charge State label missing", page.verifyChargeStatePresent())
        Assert.assertTrue("Temperature label missing", page.verifyTemperaturePresent())
    }

    @Then("I close the application")
    fun `I close the application`() {
        loginPage.closeApp()
    }

    @Then("I navigate to (.*) page$")
    fun `I navigate to settings page`(page: String) {
        when (page.trim().lowercase()) {
            "network settings" -> networkSettingsPage.clickOnNetworkSettingsButton()
            "survey settings" -> networkSettingsPage.clickOnSurveySettingsButton()
            "advanced" -> networkSettingsPage.clickOnAdvancedButton()
            else -> throw IllegalArgumentException("Unknown settings page: $page")
        }
    }

    @Then("I navigate to (.*) via navigation bar$")
    fun `I navigate to tab via navigation bar`(tab: String) {
        val page = networkSettingsPage

        when (tab.trim().lowercase()) {
            "dashboard" -> page.clickOnDashboardTab()
            "network settings" -> page.clickOnNetworkSettingsTab()
            "survey settings" -> page.clickOnSurveySettingsTab()
            "lane setup" -> page.clickOnLaneSetupTab()
            "advanced" -> page.clickOnAdvancedTab()
            else -> throw IllegalArgumentException("Unknown tab: $tab")
        }
    }

    @Then("I can disconnect the device$")
    fun `I can disconnect the device`() {
        networkSettingsPage.clickOnDisconnectDeviceButton()
    }

    @Then("I can update network settings details")
    fun `I can update network settings details`() {
        val page = networkSettingsPage
        val domain = getRandomDomainName()
        val username = "admin"
        val password = "admin"

        page.fillAccessPointName(domain)
            .fillAccessPointUserName(username)
            .fillAccessPointPassword(password)

        session().enteredDomain = domain
        session().enteredUsername = username
        session().enteredPassword = password

        page.clickSave()
        WaitUtils.waitFor(5)
        // workaround: navigate away and back so the form reloads persisted values
        page.clickOnDashboardTab()
        WaitUtils.waitFor(5)
        page.clickOnNetworkSettingsButton()
    }

    @Then("I verify inputs are persistent")
    fun `I verify inputs are persistent`() {
        val page = networkSettingsPage

        val actualDomain = page.getAccessPointName()
        val actualUsername = page.getAccessPointUsername()
        val actualPassword = page.getAccessPointPassword()

        Assert.assertEquals("Domain name mismatch", session().enteredDomain, actualDomain)
        Assert.assertEquals("Username mismatch", session().enteredUsername, actualUsername)
        Assert.assertEquals("Password mismatch", session().enteredPassword, actualPassword)
    }

    @Then("I can update survey settings details with checkboxes set to (.*)$")
    fun `I can update survey settings details`(checkBox: String) {
        val page = surveySettingsPage
        val classSchemes = listOf("DIR2", "EUR6")
        val selectedScheme = classSchemes.random()
        val checkboxStates = checkBox.toBooleanStrict()

        page.selectClassSchemeFromDropdown(selectedScheme)
            .setAllSurveyCheckboxes(checked = checkboxStates)
            .clickSave()
            .dismissWarningIfPresent()

        session().expectedClassScheme = selectedScheme
        session().expectedCheckboxesChecked = checkboxStates
    }

    @Then("I refresh the page")
    fun `I refresh the page`() {
        surveySettingsPage.clickOnRefreshButton()
    }

    @Then("I verify changes are persistent")
    fun `I verify changes are persistent`() {
        val page = surveySettingsPage
        WaitUtils.waitFor(1)

        session().expectedClassScheme?.let { expectedScheme ->
            val isClassSchemePersisted = page.verifySelectedClassScheme(expectedScheme)
            Assert.assertTrue("Class scheme is not persisted: expected '$expectedScheme'", isClassSchemePersisted)
        }

        val expectedCheckboxState = session().expectedCheckboxesChecked
        page.surveyCheckboxIds.forEach { id ->
            val isCheckboxStateCorrect = page.verifyCheckboxState(id, expectedCheckboxState)
            Assert.assertTrue("Checkbox $id is not in expected state: expected $expectedCheckboxState", isCheckboxStateCorrect)
        }
    }

    @Then("I can reboot the device")
    fun `I can reboot the device`() {
        advancedPage.clickOnRebootDeviceButton()
    }

    @Then("I can export device configuration")
    fun `I can export device configuration`() {
        advancedPage
            .clickOnExportDeviceConfigurationButton()
            .waitForExportLoaderToDisappear()

        val saved = DesktopUtils.clickSaveAndConfirmExport()
        if (!saved) {
            throw AssertionError("Save button could not be clicked")
        }
    }

    @Then("I verify the exported configuration file is valid")
    fun `I verify exported config file`() {
        val downloadDir = AppConfig.DOWNLOAD_LOCATION
        logInfo("Download location is: $downloadDir")

        val result = FileUtils.verifyExportedConfigFile(
            downloadDir,
            environmentConfig.deviceExternalId(),
            environmentConfig.deviceType()
        )
        Assert.assertTrue("Exported XML configuration file validation failed", result)
    }

    private fun getRandomDomainName(): String {
        val domains = listOf(
            "clinknet.com",
            "wlapn.com",
            "iotmesh.net",
            "vpnaccess.org",
            "devicehub.io",
            "securelink.net",
            "connex.tech"
        )
        return domains.random()
    }
}
