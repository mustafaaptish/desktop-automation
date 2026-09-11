package insight.pages

import org.openqa.selenium.By

/**
 * Locators shared across pages and native Windows dialogs.
 */
object GlobalLocators {
    val SAVE_BUTTON: By = By.name("Save")
    val CANCEL_BUTTON: By = By.name("Cancel")
    val OK_BUTTON: By = By.name("OK")
    val YES_BUTTON: By = By.name("Yes")
    val RETURN_BUTTON: By = By.name("Return")
    val DONT_SAVE_BUTTON: By = By.name("Don't save")
}
