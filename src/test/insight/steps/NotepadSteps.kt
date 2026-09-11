package insight.steps

import insight.pages.NotepadPage
import io.cucumber.java.en.Then
import org.junit.Assert

class NotepadSteps : BaseStep() {

    private val notepadPage get() = NotepadPage(driver)

    @Then("I type (.*) in notepad$")
    fun `I type something`(text: String) {
        notepadPage.enterText(text)
        session().enteredText = text
    }

    @Then("I verify the text")
    fun `I verify the text`() {
        val expected = session().enteredText
        val actual = notepadPage.getText()
        Assert.assertEquals("Text does not match", expected, actual)
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

    @Then("I close Notepad")
    fun closeNotepad() {
        notepadPage.closeNotepad()
    }
}
