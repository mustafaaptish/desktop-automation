package insight.dependencies

import insight.models.Token

/**
 * Per-scenario "world" object holding mutable state shared between steps.
 * Created lazily by BaseStep.session() and cleared by the hooks after each scenario.
 */
class Main {

    var currentToken: Token? = null

    var enteredText: String? = null
    var enteredDomain: String? = null
    var enteredUsername: String? = null
    var enteredPassword: String? = null
    var isFileSaved: Boolean = false
    var expectedClassScheme: String? = null
    var expectedCheckboxesChecked: Boolean = false
}
