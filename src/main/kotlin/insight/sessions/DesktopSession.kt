package insight.sessions

import insight.pages.ClinkDashboardPage
import insight.pages.ClinkLoginPage
import insight.pages.ClinkNetworkSettingsPage
import insight.pages.NotepadPage

/**
 * Holds session-specific data for desktop automation scenarios.
 * This allows sharing state between steps (e.g., page objects, input text, timestamps).
 */
class DesktopSession {
    lateinit var notepadPage: NotepadPage
    lateinit var clinkLoginPage: ClinkLoginPage
    lateinit var clinkDashboardPage: ClinkDashboardPage
    lateinit var clinkNetworkSettingsPage: ClinkNetworkSettingsPage

    var enteredText: String? = null
    var enteredDomain: String? = null
    var enteredUsername: String? = null
    var enteredPassword: String? = null
    var isFileSaved: Boolean = false
    // Add more properties as needed
}