package insight.utils

import insight.core.api.ResourcePaths
import insight.models.Token
import insight.utilities.log.logInfo
import insight.utilities.props.PropertyFactory
import io.restassured.RestAssured
import io.restassured.response.Response

object AuthTokenFetcher {

    fun getToken(username: String, password: String): Token {
        val environment = PropertyFactory.environmentProperty()
        val tokenUrl = environment.apiUrl().trimEnd('/') + "/" + ResourcePaths.TOKEN
        logInfo("Fetching auth token from: $tokenUrl")

        val request = RestAssured
            .given()
            .queryParam("username", username)
            .queryParam("password", password)

        // The auth endpoint validates the automation user agent when the environment defines one
        environment.userAgent().takeIf { it.isNotBlank() }?.let { request.header("User-Agent", it) }

        val response: Response = request
            .get(tokenUrl)
            .then()
            .statusCode(200)
            .extract().response()

        return response.`as`(Token::class.java)
    }
}
