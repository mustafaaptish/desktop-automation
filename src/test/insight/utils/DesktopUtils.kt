package insight.utils

import insight.drivers.DesktopDriver
import insight.pages.GlobalLocators
import insight.utilities.log.logInfo

/**
 * Helpers for native Windows dialogs that appear outside the application window.
 */
object DesktopUtils {

    fun clickSaveAndConfirmExport(timeoutInSeconds: Long = 15): Boolean {
        val endTime = System.currentTimeMillis() + timeoutInSeconds * 1000

        while (System.currentTimeMillis() < endTime) {
            try {
                val saveButton = DesktopDriver.getMyDriver().findElement(GlobalLocators.SAVE_BUTTON)
                if (saveButton.isDisplayed) {
                    saveButton.click()
                    logInfo("Clicked Save on Windows dialog")

                    // Check if "file already exists" dialog appears
                    val confirmTimeout = System.currentTimeMillis() + 5000
                    while (System.currentTimeMillis() < confirmTimeout) {
                        try {
                            val yesButton = DesktopDriver.getMyDriver().findElement(GlobalLocators.YES_BUTTON)
                            if (yesButton.isDisplayed) {
                                yesButton.click()
                                logInfo("Clicked Yes to overwrite existing file")
                                break
                            }
                        } catch (e: Exception) {
                            // Yes button not shown yet
                        }
                        Thread.sleep(500)
                    }

                    // Wait for Export Successful dialog and click OK
                    val okTimeout = System.currentTimeMillis() + 5000
                    while (System.currentTimeMillis() < okTimeout) {
                        try {
                            val okButton = DesktopDriver.getMyDriver().findElement(GlobalLocators.OK_BUTTON)
                            if (okButton.isDisplayed) {
                                okButton.click()
                                logInfo("Clicked OK on Export Successful dialog")
                                return true
                            }
                        } catch (e: Exception) {
                            // OK button not shown yet
                        }
                        Thread.sleep(500)
                    }

                    logInfo("OK button not found after Save or Overwrite confirmation")
                    return false
                }
            } catch (e: Exception) {
                logInfo("Error during Save operation: ${e.message}")
            }

            Thread.sleep(500)
        }

        logInfo("Save button not found within timeout")
        return false
    }
}
