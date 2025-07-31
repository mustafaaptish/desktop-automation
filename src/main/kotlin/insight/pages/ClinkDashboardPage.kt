package insight.pages

import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class ClinkDashboardPage(val driver: WindowsDriver<WebElement>) : BasePage(driver)  {

    private val dashboardLabel: WebElement
        get() = driver.findElementByName("Dashboard")

    private val dashboardLabelLocator = By.name("Dashboard")

    // Helper method to find the value element based on AutomationId or next sibling
    private fun findValueByLabel(labelName: String, automationId: String? = null): String? {
        return try {
            val label = driver.findElementByName(labelName)
            val valueElement: WebElement = when {
                automationId != null -> {
                    driver.findElementByAccessibilityId(automationId)
                }
//                labelName in listOf("Current Power State", "Current Connection State") -> {
//                    // For Custom elements, find the nested TextBlock
//                    val customElement: WebElement = label.findElement(By.xpath("./following-sibling::Custom[1]"))
//                    customElement.findElement(By.xpath(".//TextBlock"))
//                }
                labelName == "Current Faults" -> {
                    // For Current Faults, return empty string if no TextBlock is found
                    try {
                        label.findElement(By.xpath("./following-sibling::Custom[1]//TextBlock"))
                    } catch (e: Exception) {
                        return ""
                    }
                }
                else -> {
                    // Default case: find the next sibling TextBlock
                    label.findElement(By.xpath("./following-sibling::Text[1]//TextBlock"))
                }
            }
            valueElement.text.trim()
        } catch (e: Exception) {
            null
        }
    }

    fun waitForDashboardVisible(timeoutSeconds: Long = 30): Boolean {
        val wait = WebDriverWait(driver, timeoutSeconds)
        return try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardLabelLocator))
            println("Dashboard is visible")
            true
        } catch (e: Exception) {
            println("Dashboard not visible within $timeoutSeconds seconds")
            false
        }
    }

    fun verifyPowerStatusSectionIsPresent(): Boolean {
        return isElementVisible("Overview")
    }

    fun verifyInsightID(expectedValue: String): Boolean {
        val value = findValueByLabel("Insight ID", "txtboxDeviceId")
        return value != null && isElementVisible("Insight ID") && value == expectedValue
    }

    fun verifyInsightName(expectedValue: String): Boolean {
        val value = findValueByLabel("Insight Name", "txtboxDeviceName")
        return value != null && isElementVisible("Insight Name") && value == expectedValue
    }

    fun verifyExternalID(expectedValue: String): Boolean {
        val value = findValueByLabel("External ID", "txtboxGenDataExternalId")
        return value != null && isElementVisible("External ID") && value == expectedValue
    }

    fun verifyApplicationVersion(expectedValue: String): Boolean {
        val value = findValueByLabel("Application version", "txtboxGenDataMainApp")
        return value != null && isElementVisible("Application version") && value == expectedValue
    }

    fun verifyBootVersion(expectedValue: String): Boolean {
        val value = findValueByLabel("Boot Version", "txtboxGenDataBootLoader")
        return value != null && isElementVisible("Boot Version") && value == expectedValue
    }

    fun verifyUnitUpTime(): Boolean {
        val value = findValueByLabel("Unit Up Time", "txtboxMonDataUptime")
        if (value == null || !isElementVisible("Unit Up Time")) return false

        val regex = Regex("""\d+ weeks, \d+ days, \d+ hours, \d+ minutes""")
        return regex.matches(value)
    }

    fun verifyCurrentFaults(expectedValue: String = ""): Boolean {
        val value = findValueByLabel("Current Faults")
        return value != null && isElementVisible("Current Faults") && value == expectedValue
    }

    fun verifyOverviewSection(): Boolean {
        return isElementVisible("Overview")
    }

    fun verifyCurrentPowerStatePresent(): Boolean {
        return isElementVisible("Current Power State")
    }

    fun verifyCurrentConnectionStatePresent(): Boolean {
        return isElementVisible("Current Connection State")
    }

    // New methods to verify presence of Power Status section labels
    fun verifyPowerModePresent(): Boolean {
        return isElementVisible("Power Mode")
    }

    fun verifyExternalVoltagePresent(): Boolean {
        return isElementVisible("External Voltage")
    }

    fun verifyBatteryVoltagePresent(): Boolean {
        return isElementVisible("Battery Voltage")
    }

    fun verifyChargeStatePresent(): Boolean {
        return isElementVisible("Charge State")
    }

    fun verifyTemperaturePresent(): Boolean {
        return isElementVisible("Temperature")
    }

    private fun isElementVisible(name: String): Boolean {
        return try {
            driver.findElementByName(name).isDisplayed
        } catch (e: Exception) {
            false
        }
    }

}