package insight.steps

import insight.pages.MsTeamsPage
import insight.utilities.log.logInfo
import io.cucumber.java.en.Then
import net.datafaker.Faker

class MsTeamsSteps : BaseStep() {

    private val msTeamsPage get() = MsTeamsPage(driver)

    private val faker = Faker()

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
        page.focusMessageInput() // resolve and focus the box once, up front
        val endTime = System.currentTimeMillis() + (minutes * 60_000L)

        var messageCount = 0
        while (System.currentTimeMillis() < endTime) {
            page.typeAndClearMessage(generateRandomMessage(++messageCount))
        }
        logInfo("Finished typing random messages for $minutes minute(s), typed $messageCount messages")
    }

    // Datafaker's facts are bundled, so the typing loop never blocks on a network call
    private fun generateRandomMessage(index: Int): String = "#$index ${faker.chuckNorris().fact()}"
}
