package insight.pages

import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class ClinkSurveySettingsPage (val driver: WindowsDriver<WebElement>) : BasePage(driver) {


    private val surveySettingsText = By.name("Survey Settings")

    private val classSchemeDropdown: WebElement
        get() = driver.findElementByAccessibilityId("classScheme")

    private val refreshButton: WebElement
        get() = driver.findElementByAccessibilityId("refreshBtn")

    private val additionalClassSchemeDropdown: WebElement
        get() = driver.findElementByAccessibilityId("additionalClassScheme")


    fun clickOnRefreshButton() : ClinkSurveySettingsPage {
        refreshButton.click()
        println("Clicked on 'Refresh' button")
        return this
    }

    fun selectAdditionalClassScheme(value: String): ClinkSurveySettingsPage {
        additionalClassSchemeDropdown.click()
        Thread.sleep(500)
        additionalClassSchemeDropdown.sendKeys(value)
        additionalClassSchemeDropdown.sendKeys(Keys.ENTER)
        println("Selected additional class scheme: $value")
        return this
    }

    fun clickSave(): ClinkSurveySettingsPage {
        saveButton.click()
        println("Clicked Save button")
        return this
    }

    fun getSelectedAdditionalClassScheme(): String {
        return additionalClassSchemeDropdown.text
    }

    private val saveButton: WebElement
        get() = driver.findElementByAccessibilityId("FormButton_Save")

    fun verifyCheckboxState(id: String, expectedChecked: Boolean): Boolean {
        val actual = isCheckboxChecked(id)
        println("Checkbox $id is ${if (actual) "checked" else "unchecked"} (expected: $expectedChecked)")
        return actual == expectedChecked
    }

    fun verifySelectedClassScheme(expected: String): Boolean {
        val actual = classSchemeDropdown.text
        println("Class scheme selected: '$actual' (expected: '$expected')")
        return actual == expected
    }

    fun waitForSurveySettingsToBeVisible(timeoutSeconds: Long = 30): Boolean {
        val wait = WebDriverWait(driver, timeoutSeconds)
        return try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(surveySettingsText))
            println("Survey Settings is visible")
            true
        } catch (e: Exception) {
            println("Survey Settings not visible within $timeoutSeconds seconds")
            false
        }
    }

    val surveyCheckboxIds = listOf(
        "vehicleRecord_class",
        "vehicleRecord_direction",
        "vehicleRecord_gap",
        "vehicleRecord_lane",
        "vehicleRecord_length",
        "vehicleRecord_speed"
    )

    fun setAllSurveyCheckboxes(checked: Boolean): ClinkSurveySettingsPage {
        for (id in surveyCheckboxIds) {
            val checkbox = checkboxById(id)
            val isChecked = checkbox.isSelected
            if (isChecked != checked) {
                checkbox.click()
                println("${if (checked) "Checked" else "Unchecked"} checkbox: $id")
            } else {
                println("Checkbox $id already ${if (checked) "checked" else "unchecked"}")
            }
        }
        return this
    }

    /**
     // Survey Settings locators
    AutomationId:	"vehicleRecord_class"
    AutomationId:	"vehicleRecord_direction"
    AutomationId:	"vehicleRecord_gap"
    AutomationId:	"vehicleRecord_lane"
    AutomationId:	"vehicleRecord_length"
    AutomationId:	"vehicleRecord_speed"
    AutomationId:	"FormButton_Save"

    //Additional Class Scheme locators
    AutomationId:	"additionalClassScheme"
    Name:	"Download"
    AutomationId:	"FormButton_Cancel"
***/

    private fun checkboxById(id: String): WebElement = driver.findElementByAccessibilityId(id)
    fun isCheckboxChecked(id: String): Boolean = checkboxById(id).isSelected

    fun checkCheckbox(id: String): ClinkSurveySettingsPage {
        val checkbox = checkboxById(id)
        if (!checkbox.isSelected) checkbox.click()
        println("Checked checkbox: $id")
        return this
    }

    fun uncheckCheckbox(id: String): ClinkSurveySettingsPage {
        val checkbox = checkboxById(id)
        if (checkbox.isSelected) checkbox.click()
        println("Unchecked checkbox: $id")
        return this
    }

    fun clickOnClassSchemeDropdown() : ClinkSurveySettingsPage {
        classSchemeDropdown.click()
        println("Clicked on Class Scheme dropdown")
        return this
    }

    fun selectClassSchemeFromDropdown(classScheme: String): ClinkSurveySettingsPage {
        clickOnClassSchemeDropdown()
        WebDriverWait(driver, 5).until(ExpectedConditions.elementToBeClickable(classSchemeDropdown))
        classSchemeDropdown.sendKeys(classScheme)
        classSchemeDropdown.sendKeys(Keys.ENTER)
        println("Selected Class Scheme: '$classScheme'")
        return this
    }


}