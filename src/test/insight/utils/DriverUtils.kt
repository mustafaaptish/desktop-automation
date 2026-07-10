package insight.utils

import org.openqa.selenium.StaleElementReferenceException

object DriverUtils {

    fun <T> retryOnStaleElement(retries: Int = 3, waitBetweenMs: Long = 200, block: () -> T): T {
        var lastException: StaleElementReferenceException? = null
        repeat(retries) {
            try {
                return block()
            } catch (e: StaleElementReferenceException) {
                lastException = e
                Thread.sleep(waitBetweenMs)
            }
        }
        throw lastException ?: IllegalStateException("retryOnStaleElement failed without exception")
    }
}
