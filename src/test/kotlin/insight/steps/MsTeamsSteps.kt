package insight.steps

import insight.utilities.AuthTokenFetcher
import insight.utilities.Credentials
import insight.utilities.DesktopUtils
import insight.utilities.FileUtils
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert
import java.lang.AssertionError

class MsTeamsSteps: DesktopBaseStep() {
//    private lateinit var clinkPage: ClinkPage


    @Then("I click on my own chat")
    fun `I click on my own chat`() {
        session().msTeamsPage.clickOnMyChat()
    }

    @Then("I type {string} in the message box")
    fun `I type message in the message box`(message: String) {
        session().msTeamsPage.typeMessage(message)
    }

    @Then("I type random text in the message box for (.*) minutes$")
    fun `I type random text for duration`(minutes: Int) {
        val page = session().msTeamsPage
        val endTime = System.currentTimeMillis() + (minutes * 60_000L)

        var messageCount = 0
        while (System.currentTimeMillis() < endTime) {
            val message = generateRandomMessage(++messageCount)
            page.typeMessage(message)
//            page.sendMessage()
//            Thread.sleep(2_000)
            page.clearMessage()
        }
        println("✅ Finished typing random messages for $minutes minute(s), sent $messageCount messages")
    }

//    @Then("I dump Teams element tree")
//    fun `I dump Teams element tree`() {
//        val page = session().msTeamsPage
//        page.dumpElementTree()
//    }

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