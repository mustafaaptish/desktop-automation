package insight.utilities

import insight.drivers.DesktopDriver

object DesktopUtils {

    fun clickSaveAndConfirmExport(timeoutInSeconds: Long = 15): Boolean {
        val endTime = System.currentTimeMillis() + timeoutInSeconds * 1000

        while (System.currentTimeMillis() < endTime) {
            try {
                val saveButton = DesktopDriver.getDriver().findElementByName("Save")
                if (saveButton.isDisplayed) {
                    saveButton.click()
                    println("Clicked Save on Windows dialog")

                    // Check if file already exists dialog appears
                    val confirmTimeout = System.currentTimeMillis() + 5000
                    while (System.currentTimeMillis() < confirmTimeout) {
                        try {
                            val yesButton = DesktopDriver.getDriver().findElementByName("Yes")
                            if (yesButton.isDisplayed) {
                                yesButton.click()
                                println("Clicked Yes to overwrite existing file")
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
                            val okButton = DesktopDriver.getDriver().findElementByName("OK")
                            if (okButton.isDisplayed) {
                                okButton.click()
                                println("Clicked OK on Export Successful dialog")
                                return true
                            }
                        } catch (e: Exception) {
                            // OK button not shown yet
                        }
                        Thread.sleep(500)
                    }

                    println("OK button not found after Save or Overwrite confirmation")
                    return false
                }
            } catch (e: Exception) {
                println("Error during Save operation: ${e.message}")
            }

            Thread.sleep(500)
        }

        println("Save button not found within timeout")
        return false
    }
}