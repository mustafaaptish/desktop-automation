package insight.hooks

import insight.drivers.DesktopDriver
import insight.pages.MsTeamsPage
import insight.steps.BaseStep.Companion.clearSession
import insight.steps.BaseStep.Companion.session
import insight.utilities.log.logError
import insight.utilities.log.logInfo
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.Scenario
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot

/**
 * Generic driver/session lifecycle around each scenario.
 */
class Hooks {

    @Before
    fun setUp(scenario: Scenario) {
        session()
        logInfo("Starting scenario: ${scenario.name}")
    }

    @After(order = 20)
    fun tearDown(scenario: Scenario) {
        logInfo("Finished scenario: ${scenario.name}")

        val driver = runCatching { DesktopDriver.getMyDriver() }.getOrNull()

        if (driver != null) {
            if (scenario.isFailed) {
                try {
                    val screenshot = (driver as? TakesScreenshot)?.getScreenshotAs(OutputType.BYTES)
                    if (screenshot != null) {
                        scenario.attach(screenshot, "image/png", "Screenshot for failed scenario: ${scenario.name}")
                    }
                } catch (ex: Exception) {
                    logError("Error capturing screenshot: ${ex.message}")
                }
            }

            logInfo("Closing desktop driver for scenario: ${scenario.name}")
            DesktopDriver.closeDriver()
        }

        MsTeamsPage.resetCache() // cached elements belong to the session we just closed
        logInfo("Clearing session for scenario: ${scenario.name}")
        clearSession()
    }
}
