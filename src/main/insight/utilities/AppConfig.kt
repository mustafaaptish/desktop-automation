package insight.utilities

/**
 * File-system path constants used across the framework.
 */
object AppConfig {

    val DOWNLOAD_LOCATION: String =
        System.getProperty("download.dir") ?: System.getProperty("user.dir")
}
