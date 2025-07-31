package insight.sessions

import insight.pages.*

/**
 * Holds session-specific data for desktop automation scenarios.
 * This allows sharing state between steps (e.g., page objects, input text, timestamps).
 */
class DesktopSession {
    lateinit var notepadPage: NotepadPage
    lateinit var clinkLoginPage: ClinkLoginPage
    lateinit var clinkDashboardPage: ClinkDashboardPage
    lateinit var clinkNetworkSettingsPage: ClinkNetworkSettingsPage
    lateinit var clinkSurveySettingsPage: ClinkSurveySettingsPage

    var enteredText: String? = null
    var enteredDomain: String? = null
    var enteredUsername: String? = null
    var enteredPassword: String? = null
    var isFileSaved: Boolean = false
    var expectedClassScheme: String? = null
    var expectedCheckboxesChecked: Boolean = false
    // Add more properties as needed
}