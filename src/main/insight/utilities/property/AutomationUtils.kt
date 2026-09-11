package insight.utilities.property

import insight.utilities.props.AutomationConfig
import org.aeonbits.owner.ConfigFactory

/**
 * Bridges runtime system properties and property-file defaults:
 * a value passed with -D<name> always wins over the properties file.
 */
object AutomationUtils {

    private val automationConfig: AutomationConfig = ConfigFactory.create(AutomationConfig::class.java)

    fun getCurrentEnvironment(): String =
        System.getProperty("Environment").takeUnless { it.isNullOrBlank() }
            ?: automationConfig.environment()

    fun getUsername(): String =
        System.getProperty("username").takeUnless { it.isNullOrBlank() }
            ?: automationConfig.username()

    fun getPassword(): String =
        System.getProperty("password").takeUnless { it.isNullOrBlank() }
            ?: automationConfig.password()
}
