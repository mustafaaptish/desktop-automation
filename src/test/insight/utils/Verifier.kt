package insight.utils

import org.junit.Assert

/**
 * Polling assertion helper: retries a boolean question until it passes or the timeout expires.
 */
class Verifier {

    companion object {

        fun verify(failMessage: String, condition: Boolean) {
            Assert.assertTrue(failMessage, condition)
        }

        fun verifyWithinTime(
            failMessage: String,
            timeoutSeconds: Long = 30,
            pollIntervalMillis: Long = 500,
            question: () -> Boolean
        ) {
            val endTime = System.currentTimeMillis() + timeoutSeconds * 1000
            while (System.currentTimeMillis() < endTime) {
                if (runCatching(question).getOrDefault(false)) return
                Thread.sleep(pollIntervalMillis)
            }
            Assert.fail(failMessage)
        }
    }
}
