package insight.utils

import insight.models.Token
import insight.steps.BaseStep
import insight.utilities.property.AutomationUtils

/**
 * Fetches (and caches per scenario) auth tokens for the automation user.
 */
object TokenUtils : BaseStep() {

    fun systemAdminToken(): Token {
        session().currentToken?.let { return it }
        val token = AuthTokenFetcher.getToken(AutomationUtils.getUsername(), AutomationUtils.getPassword())
        session().currentToken = token
        return token
    }
}
