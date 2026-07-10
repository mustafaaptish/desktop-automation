package insight.pages

import insight.utilities.log.logInfo
import insight.utils.WaitUtils
import io.appium.java_client.windows.WindowsDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class ClinkAdvancedPage(private val driver: WindowsDriver<WebElement>) : BasePage(driver) {

    private val rebootDeviceButton: By = By.name("Reboot Device")
    private val downloadLatestFirmwareButton: By = By.name("Download Latest Firmware")
    private val exportDeviceConfigButton: By = By.name("Export Device Configuration")
    private val importDeviceConfigButton: By = By.name("Import Device Configuration")
    private val exportDeviceConfigLoader: By = By.name("Exporting device config")

    fun clickOnRebootDeviceButton(): ClinkAdvancedPage {
        click(rebootDeviceButton)
        logInfo("Clicked on 'Reboot Device' button")
        return this
    }

    fun clickOnDownloadLatestFirmwareButton(): ClinkAdvancedPage {
        click(downloadLatestFirmwareButton)
        logInfo("Clicked on 'Download Latest Firmware' button")
        return this
    }

    fun clickOnExportDeviceConfigurationButton(): ClinkAdvancedPage {
        click(exportDeviceConfigButton)
        logInfo("Clicked on 'Export Device Configuration' button")
        return this
    }

    fun clickOnImportDeviceConfigurationButton(): ClinkAdvancedPage {
        click(importDeviceConfigButton)
        logInfo("Clicked on 'Import Device Configuration' button")
        return this
    }

    fun waitForExportLoaderToDisappear(timeoutInSeconds: Long = DEFAULT_WAIT_TIME): ClinkAdvancedPage {
        try {
            WaitUtils.waitForInvisibility(exportDeviceConfigLoader, timeoutInSeconds)
            logInfo("Export loader has disappeared.")
        } catch (e: Exception) {
            throw AssertionError("Timeout waiting for export loader to disappear after $timeoutInSeconds seconds")
        }
        WaitUtils.waitForMilliSec(1000) // buffer for the save dialog to appear
        return this
    }
}
