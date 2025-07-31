package insight.steps

import insight.pages.ClinkDashboardPage
import insight.pages.ClinkLoginPage
import insight.pages.ClinkNetworkSettingsPage
import insight.pages.NotepadPage
import insight.drivers.DesktopDriver
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import org.junit.Assert

class NotepadSteps: DesktopBaseStep() {
    private lateinit var notepadPage: NotepadPage
    private lateinit var clinkPage: ClinkLoginPage



    @Given("I launch (.*) application$")
    fun `I launch the application`(appName: String) {
        val driver = DesktopDriver.startApp(appName)
        when (appName.lowercase()) {
            "notepad" -> {
                val page = NotepadPage(driver)
                notepadPage = page
                session().notepadPage = page
            }
            "clink" -> {
                val clinkPage = ClinkLoginPage(driver)
                val dashboardPage = ClinkDashboardPage(driver)
                val clinkNetworkSettingsPage = ClinkNetworkSettingsPage(driver)

                this.clinkPage = clinkPage
                session().clinkLoginPage = clinkPage
                session().clinkDashboardPage = dashboardPage
                session().clinkNetworkSettingsPage = clinkNetworkSettingsPage
            }
            else -> throw IllegalArgumentException("Unsupported app: $appName")
        }
    }


    @Then("I type (.*)$")
    fun `I type something`(text: String) {
        notepadPage.enterText(text)
        session().enteredText = text
    }

    @Then("I verify the text")
    fun `I verify the text`() {
        val expected = session().enteredText
        val actual = notepadPage.getText()
        Assert.assertEquals("Text does not match", actual, expected)
    }

    @Then("I clear the text")
    fun `I clear the text`() {
        notepadPage.clearText()
    }

    @Then("I open a new tab")
    fun `I open a new tab`() {
        notepadPage.clickOnAddNewTab()
    }

    @Then("I click do not save button")
    fun `I click do not save button`() {
        notepadPage.clickOnDoNotSave()
    }

    @Then("I wait for {int} seconds")
    fun `I wait for seconds`(seconds: Int) {
        Thread.sleep(seconds * 1000L)
    }


//    @Then("I close the tab")
//    fun `I close the tab`() {
//        notepadPage.closeTab()
//    }
//
//
//    @Then("I close all tabs")
//    fun closeAllTabs() {
//        notepadPage.closeAllTabs()
//    }

    @Then("I close Notepad")
    fun closeNotepad() {
        notepadPage.closeNotepad()
    }
}