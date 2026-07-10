package insight.steps

import insight.pages.MsTeamsPage
import insight.utilities.log.logInfo
import io.cucumber.java.en.Then

class MsTeamsSteps : BaseStep() {

    private val msTeamsPage get() = MsTeamsPage(driver)

    @Then("I click on my own chat")
    fun `I click on my own chat`() {
        msTeamsPage.clickOnMyChat()
    }

    @Then("I type {string} in the message box")
    fun `I type message in the message box`(message: String) {
        msTeamsPage.typeMessage(message)
    }

    @Then("I type random text in the message box for (.*) minutes$")
    fun `I type random text for duration`(minutes: Int) {
        val page = msTeamsPage
        val endTime = System.currentTimeMillis() + (minutes * 60_000L)

        var messageCount = 0
        while (System.currentTimeMillis() < endTime) {
            val message = generateRandomMessage(++messageCount)
            page.typeMessage(message)
            page.clearMessage()
        }
        logInfo("Finished typing random messages for $minutes minute(s), typed $messageCount messages")
    }

    private fun generateRandomMessage(index: Int): String {
        val samples = listOf(
            "Automated test message",
            "Testing Teams automation",
            "Hello from automation framework",
            "Verification message",
            "Desktop automation test"
        )
        return "${samples[index % samples.size]} #$index - ${System.currentTimeMillis()}"
    }
}
