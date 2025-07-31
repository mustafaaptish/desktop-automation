package insight.steps

import insight.utilities.AuthTokenFetcher
import insight.utilities.Credentials
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert

class ClinkSteps: DesktopBaseStep() {
//    private lateinit var clinkPage: ClinkPage


    @Then("I click connect to a device$")
    fun `I click connect to a device`() {
        val clinkPage = session().clinkLoginPage
       clinkPage.clickConnectToDevice()

    }
    @Then("I enter my Clink credentials and login")
    fun `I enter my Clink credentials and login`() {
        val clinkPage = session().clinkLoginPage

        clinkPage.
            enterUsername(Credentials.username).
                enterPassword(Credentials.password).
                    clickLogin()
    }

    @When("I enter my Insight Token and login")
        fun `I enter my Insight Token and login`() {
        val page = session().clinkLoginPage
        // Obtain token from API
        val token = AuthTokenFetcher.getToken(Credentials.username, Credentials.password).token

        page.
            selectInsightTokenRadioButton().
                enterToken(token).
                    clickLoginWithTokenButton()
    }


    @Then("I should see the (.*) screen$")
    fun `I should see the Dashboard screen`(screenName: String) {
            val isVisible = when (screenName.lowercase()) {
                "dashboard" -> session().clinkDashboardPage.waitForDashboardVisible()
                "1. network settings" -> session().clinkNetworkSettingsPage.waitForNetworkSettingsToBeVisible()
                // Add more screen checks as needed
                else -> throw IllegalArgumentException("No visibility check defined for screen: $screenName")
            }

            Assert.assertTrue("$screenName screen is not visible", isVisible)
    }

    @Then("I should see correct details")
    fun `I should see correct details`() {
        val dashboardPage = session().clinkDashboardPage
//        println(dashboardPage.driver.pageSource)

        Assert.assertTrue("Dashboard not visible", dashboardPage.waitForDashboardVisible())
        Assert.assertTrue("Overview section missing", dashboardPage.verifyOverviewSection())
        Assert.assertTrue("Insight ID missing or incorrect", dashboardPage.verifyInsightID("111105"))
        Assert.assertTrue("Insight Name missing or incorrect", dashboardPage.verifyInsightName("Connex Stefan Desk"))
        Assert.assertTrue("External ID missing or incorrect", dashboardPage.verifyExternalID("mbLeHR3g"))
        Assert.assertTrue("Application version missing or incorrect", dashboardPage.verifyApplicationVersion("V1.22.23 (760653)"))
        Assert.assertTrue("Boot Version missing or incorrect", dashboardPage.verifyBootVersion("V1.1.0 (760618)"))
        Assert.assertTrue("Unit Up Time missing or incorrect", dashboardPage.verifyUnitUpTime())

        Assert.assertTrue("Current Power State missing or incorrect", dashboardPage.verifyCurrentPowerStatePresent())
        Assert.assertTrue("Current Connection State missing or incorrect", dashboardPage.verifyCurrentConnectionStatePresent())
        Assert.assertTrue("Current Faults missing or incorrect", dashboardPage.verifyCurrentFaults(""))
        Assert.assertTrue("Power Status section missing", dashboardPage.verifyPowerStatusSectionIsPresent())
        Assert.assertTrue("Power Mode label missing", dashboardPage.verifyPowerModePresent())
        Assert.assertTrue("External Voltage label missing", dashboardPage.verifyExternalVoltagePresent())
        Assert.assertTrue("Battery Voltage label missing", dashboardPage.verifyBatteryVoltagePresent())
        Assert.assertTrue("Charge State label missing", dashboardPage.verifyChargeStatePresent())
        Assert.assertTrue("Temperature label missing", dashboardPage.verifyTemperaturePresent())

    }

    @Then("I close the application")
    fun `I close the application`() {
        val clinkPage = session().clinkLoginPage
        clinkPage.closeApp()
    }

    @Then("I select a device from the dropdown")
    fun `I select a device from the dropdown`() {
        val clinkPage = session().clinkLoginPage
        clinkPage
            .clickOnDevicesDropdown()
             .selectDeviceFromDropdown("Connex Stefan Desk (111105)")
                .clickOnSelectButton()
    }


    @Then("I navigate to Network Settings")
    fun `I navigate to Network Settings`() {
        val clinkNetworkSettingsPage = session().clinkNetworkSettingsPage
        clinkNetworkSettingsPage.clickOnNetworkSettingsButton()
    }


    @Then("I can update network settings details")
    fun `I can update network settings details`() {
        val clinkNetworkSettingsPage = session().clinkNetworkSettingsPage
        val domain = getRandomDomainName()
        val username = "admin"
        val password = "admin"
        clinkNetworkSettingsPage.fillAccessPointName(domain)
        clinkNetworkSettingsPage.fillAccessPointUserName(username)
        clinkNetworkSettingsPage.fillAccessPointPassword(password)

        session().enteredDomain = domain
        session().enteredUsername = username
        session().enteredPassword = password

        clinkNetworkSettingsPage.clickSave()
        Thread.sleep(5000)
        // workaround
        clinkNetworkSettingsPage.clickOnDashboardTab()
        Thread.sleep(5000)
        clinkNetworkSettingsPage.clickOnNetworkSettingsButton()

    }

    fun getRandomDomainName(): String {
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


    @Then("I verify inputs are persistent")
    fun `I verify inputs are persistent`() {
        val page = session().clinkNetworkSettingsPage

        val actualDomain = page.getAccessPointName()
        val actualUsername = page.getAccessPointUsername()
        val actualPassword = page.getAccessPointPassword()
        println(actualDomain)
        println(actualUsername)
        println(actualPassword)

        Assert.assertEquals("Domain name mismatch", session().enteredDomain, actualDomain)
        Assert.assertEquals("Username mismatch", session().enteredUsername, actualUsername)
        Assert.assertEquals("Password mismatch", session().enteredPassword, actualPassword)
    }


}