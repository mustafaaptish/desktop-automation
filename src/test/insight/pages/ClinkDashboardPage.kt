package insight.pages

import insight.utilities.log.logInfo
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class ClinkDashboardPage(private val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    private val dashboardLabel: By = By.name("Dashboard")

    // Finds the value element next to a label, either by AutomationId or next sibling
    private fun findValueByLabel(labelName: String, automationId: String? = null): String? {
        return try {
            val valueElement: WebElement = when {
                automationId != null -> findElement(byAccessibilityId(automationId))
                labelName == "Current Faults" -> {
                    // For Current Faults, return empty string if no TextBlock is found
                    try {
                        findElement(By.name(labelName))
                            .findElement(By.xpath("./following-sibling::Custom[1]//TextBlock"))
                    } catch (e: Exception) {
                        return ""
                    }
                }
                else -> {
                    findElement(By.name(labelName))
                        .findElement(By.xpath("./following-sibling::Text[1]//TextBlock"))
                }
            }
            valueElement.text.trim()
        } catch (e: Exception) {
            null
        }
    }

    fun waitForDashboardVisible(timeoutSeconds: Long = DEFAULT_WAIT_TIME): Boolean {
        val visible = isElementDisplayed(dashboardLabel, timeoutSeconds)
        logInfo(if (visible) "Dashboard is visible" else "Dashboard not visible within $timeoutSeconds seconds")
        return visible
    }

    fun verifyOverviewSection(): Boolean = isLabelVisible("Overview")

    fun verifyPowerStatusSectionIsPresent(): Boolean = isLabelVisible("Overview")

    fun verifyInsightID(expectedValue: String): Boolean {
        val value = findValueByLabel("Insight ID", "txtboxDeviceId")
        return value != null && isLabelVisible("Insight ID") && value == expectedValue
    }

    fun verifyInsightName(expectedValue: String): Boolean {
        val value = findValueByLabel("Insight Name", "txtboxDeviceName")
        return value != null && isLabelVisible("Insight Name") && value == expectedValue
    }

    fun verifyExternalID(expectedValue: String): Boolean {
        val value = findValueByLabel("External ID", "txtboxGenDataExternalId")
        return value != null && isLabelVisible("External ID") && value == expectedValue
    }

    fun verifyApplicationVersion(expectedValue: String): Boolean {
        val value = findValueByLabel("Application version", "txtboxGenDataMainApp")
        return value != null && isLabelVisible("Application version") && value == expectedValue
    }

    fun verifyBootVersion(expectedValue: String): Boolean {
        val value = findValueByLabel("Boot Version", "txtboxGenDataBootLoader")
        return value != null && isLabelVisible("Boot Version") && value == expectedValue
    }

    fun verifyUnitUpTime(): Boolean {
        val value = findValueByLabel("Unit Up Time", "txtboxMonDataUptime")
        if (value == null || !isLabelVisible("Unit Up Time")) return false

        val regex = Regex("""\d+ weeks, \d+ days, \d+ hours, \d+ minutes""")
        return regex.matches(value)
    }

    fun verifyCurrentFaults(expectedValue: String = ""): Boolean {
        val value = findValueByLabel("Current Faults")
        return value != null && isLabelVisible("Current Faults") && value == expectedValue
    }

    fun verifyCurrentPowerStatePresent(): Boolean = isLabelVisible("Current Power State")

    fun verifyCurrentConnectionStatePresent(): Boolean = isLabelVisible("Current Connection State")

    fun verifyPowerModePresent(): Boolean = isLabelVisible("Power Mode")

    fun verifyExternalVoltagePresent(): Boolean = isLabelVisible("External Voltage")

    fun verifyBatteryVoltagePresent(): Boolean = isLabelVisible("Battery Voltage")

    fun verifyChargeStatePresent(): Boolean = isLabelVisible("Charge State")

    fun verifyTemperaturePresent(): Boolean = isLabelVisible("Temperature")

    private fun isLabelVisible(name: String): Boolean = isElementImmediatelyDisplayed(By.name(name))
}
