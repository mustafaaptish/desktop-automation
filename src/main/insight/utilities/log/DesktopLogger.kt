package insight.utilities.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory

private const val LOG_NAME = "Logger"

private val logger: Logger = LoggerFactory.getLogger(LOG_NAME)

fun logInfo(message: String) = logger.info(message)

fun logWarn(message: String) = logger.warn(message)

fun logError(message: String, throwable: Throwable? = null) {
    if (throwable != null) logger.error(message, throwable) else logger.error(message)
}
