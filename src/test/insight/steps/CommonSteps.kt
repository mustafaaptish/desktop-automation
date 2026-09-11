package insight.steps

import insight.drivers.DesktopDriver
import insight.utils.WaitUtils
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then

class CommonSteps : BaseStep() {

    @Given("I launch (.*) application$")
    fun `I launch the application`(appName: String) {
        DesktopDriver.createAndStoreDriver(appName)
    }

    @Then("I wait for {int} seconds")
    fun `I wait for seconds`(seconds: Int) {
        WaitUtils.waitFor(seconds.toLong())
    }
}
