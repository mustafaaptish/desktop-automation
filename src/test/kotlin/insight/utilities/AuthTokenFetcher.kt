package insight.utilities

import io.restassured.RestAssured
import io.restassured.response.Response

data class Token(val token: String)

object AuthTokenFetcher {

    private const val AUTH_URL = "https://functional-test-microservices.clearview-intelligence.io/customer-api/v3/auth"


    fun getToken(username: String, password: String): Token {
        val response: Response = RestAssured
            .given()
            .queryParam("username", username)
            .queryParam("password", password)
            .get(AUTH_URL)
            .then()
            .statusCode(200)
            .extract().response()

        return response.`as`(Token::class.java)
    }
}
