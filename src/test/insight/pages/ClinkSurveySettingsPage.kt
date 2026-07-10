package insight.pages

import insight.utilities.log.logInfo
import insight.utils.WaitUtils
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

class ClinkSurveySettingsPage(private val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    private val surveySettingsText: By = By.name("Survey Settings")
    private val classSchemeDropdown: By = byAccessibilityId("classScheme")
    private val additionalClassSchemeDropdown: By = byAccessibilityId("additionalClassScheme")
    private val refreshButton: By = byAccessibilityId("refreshBtn")
    private val saveButton: By = byAccessibilityId("FormButton_Save")

    val surveyCheckboxIds = listOf(
        "vehicleRecord_class",
        "vehicleRecord_direction",
        "vehicleRecord_gap",
        "vehicleRecord_lane",
        "vehicleRecord_length",
        "vehicleRecord_speed"
    )

    fun waitForSurveySettingsToBeVisible(timeoutSeconds: Long = DEFAULT_WAIT_TIME): Boolean {
        val visible = isElementDisplayed(surveySettingsText, timeoutSeconds)
        logInfo(if (visible) "Survey Settings is visible" else "Survey Settings not visible within $timeoutSeconds seconds")
        return visible
    }

    fun dismissWarningIfPresent(): ClinkSurveySettingsPage {
        if (isElementDisplayed(GlobalLocators.RETURN_BUTTON, timeoutInSeconds = 2)) {
            click(GlobalLocators.RETURN_BUTTON, timeoutInSeconds = 2)
            logInfo("Warning dialog detected and 'Return' clicked.")
        } else {
            logInfo("No warning dialog appeared.")
        }
        return this
    }

    fun clickOnRefreshButton(): ClinkSurveySettingsPage {
        click(refreshButton)
        logInfo("Clicked on 'Refresh' button")
        return this
    }

    fun clickSave(): ClinkSurveySettingsPage {
        click(saveButton)
        logInfo("Clicked Save button")
        return this
    }

    fun clickOnClassSchemeDropdown(): ClinkSurveySettingsPage {
        click(classSchemeDropdown)
        logInfo("Clicked on Class Scheme dropdown")
        return this
    }

    fun selectClassSchemeFromDropdown(classScheme: String): ClinkSurveySettingsPage {
        clickOnClassSchemeDropdown()
        val dropdown = WaitUtils.waitForClickAbility(classSchemeDropdown, timeoutInSeconds = 5)
        dropdown.sendKeys(classScheme)
        dropdown.sendKeys(Keys.ENTER)
        logInfo("Selected Class Scheme: '$classScheme'")
        return this
    }

    fun selectAdditionalClassScheme(value: String): ClinkSurveySettingsPage {
        click(additionalClassSchemeDropdown)
        WaitUtils.waitForMilliSec(500)
        val dropdown = findElement(additionalClassSchemeDropdown)
        dropdown.sendKeys(value)
        dropdown.sendKeys(Keys.ENTER)
        logInfo("Selected additional class scheme: $value")
        return this
    }

    fun getSelectedAdditionalClassScheme(): String = getText(additionalClassSchemeDropdown)

    fun verifySelectedClassScheme(expected: String): Boolean {
        val actual = getText(classSchemeDropdown)
        logInfo("Class scheme selected: '$actual' (expected: '$expected')")
        return actual == expected
    }

    fun verifyCheckboxState(id: String, expectedChecked: Boolean): Boolean {
        val actual = isCheckboxChecked(id)
        logInfo("Checkbox $id is ${if (actual) "checked" else "unchecked"} (expected: $expectedChecked)")
        return actual == expectedChecked
    }

    fun isCheckboxChecked(id: String): Boolean = checkboxById(id).isSelected

    fun checkCheckbox(id: String): ClinkSurveySettingsPage {
        val checkbox = checkboxById(id)
        if (!checkbox.isSelected) checkbox.click()
        logInfo("Checked checkbox: $id")
        return this
    }

    fun uncheckCheckbox(id: String): ClinkSurveySettingsPage {
        val checkbox = checkboxById(id)
        if (checkbox.isSelected) checkbox.click()
        logInfo("Unchecked checkbox: $id")
        return this
    }

    fun setAllSurveyCheckboxes(checked: Boolean): ClinkSurveySettingsPage {
        WaitUtils.waitForMilliSec(500) // Allow UI to settle before starting
        for (id in surveyCheckboxIds) {
            val maxRetries = 3
            var attempt = 0
            var success = false

            while (attempt < maxRetries) {
                val checkbox = checkboxById(id)

                if (checkbox.isSelected != checked) {
                    checkbox.click()
                    WaitUtils.waitForMilliSec(300) // Allow flicker to finish
                }

                // Re-fetch and double check
                if (checkboxById(id).isSelected == checked) {
                    logInfo("Checkbox $id successfully set to: $checked")
                    success = true
                    break
                }

                logInfo("Retrying checkbox $id. Attempt: ${attempt + 1}")
                attempt++
                WaitUtils.waitForMilliSec(200) // Allow state to settle again before retry
            }

            if (!success) {
                // Last effort: click once more and final check
                checkboxById(id).click()
                WaitUtils.waitForMilliSec(300)
                if (checkboxById(id).isSelected != checked) {
                    throw AssertionError("Checkbox $id could not be reliably set to $checked after extra attempt.")
                }
                logInfo("Checkbox $id recovered after extra attempt.")
            }
        }
        return this
    }

    private fun checkboxById(id: String): WebElement = findElement(byAccessibilityId(id))
}
