package insight.pages

import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.WebElement

class ClinkAdvancedPage(val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    private val rebootDeviceButton: WebElement
        get() = driver.findElementByName("Reboot Device")

    private val downloadLatestFirmwareButton: WebElement
        get() = driver.findElementByName("Download Latest Firmware")

    private val exportDeviceConfigButton: WebElement
        get() = driver.findElementByName("Export Device Configuration")

    private val importDeviceConfigButton: WebElement
        get() = driver.findElementByName("Import Device Configuration")

    private val resettingDeviceLoader: WebElement
        get() = driver.findElementByName("Resetting device")

    private val exportDeviceConfigLoader: WebElement
        get() = driver.findElementByName("Exporting device config")


    fun clickOnRebootDeviceButton(): ClinkAdvancedPage {
        rebootDeviceButton.click()
        println("Clicked on 'Reboot Device' button")
        return this
    }

    fun clickOnDownloadLatestFirmwareButton(): ClinkAdvancedPage {
        downloadLatestFirmwareButton.click()
        println("Clicked on 'Download Latest Firmware")
        return this
    }

    fun clickOnExportDeviceConfigurationButton(): ClinkAdvancedPage {
        exportDeviceConfigButton.click()
        println("Clicked on 'Export Device Configuration' button")
        return this
    }

    fun importDeviceConfigButton(): ClinkAdvancedPage {
        importDeviceConfigButton.click()
        println("Clicked on 'Import Device Configuration' button")
        return this
    }

    fun waitForExportLoaderToDisappear(timeoutInSeconds: Long = 30): ClinkAdvancedPage {
        val pollingIntervalMs = 500L
        val endTime = System.currentTimeMillis() + timeoutInSeconds * 1000

        while (System.currentTimeMillis() < endTime) {
            try {
                if (!exportDeviceConfigLoader.isDisplayed) {
                    println("Export loader has disappeared.")
                    Thread.sleep(1000) // buffer for dialog to appear
                    return this
                }
            } catch (e: org.openqa.selenium.NoSuchElementException) {
                // Element not found means loader disappeared
                println("Export loader element not found, assuming disappeared.")
                Thread.sleep(1000)
                return this
            } catch (e: org.openqa.selenium.StaleElementReferenceException) {
                // Element refreshed, try again next poll
            } catch (e: Exception) {
                println("Unexpected exception while waiting for loader: ${e.message}")
            }

            Thread.sleep(pollingIntervalMs)
        }

        throw AssertionError("Timeout waiting for export loader to disappear after $timeoutInSeconds seconds")
    }




}