package insight.utilities.props

import insight.utilities.property.AutomationUtils
import org.aeonbits.owner.ConfigFactory

/**
 * Single access point for typed configuration objects.
 */
object PropertyFactory {

    init {
        // Resolves the ${env} variable used by EnvironmentConfig's @Sources.
        // Lowercased so both -DEnvironment=AutoTest and -DEnvironment=autotest work.
        ConfigFactory.setProperty("env", AutomationUtils.getCurrentEnvironment().lowercase())
    }

    fun automationProperty(): AutomationConfig = ConfigFactory.create(AutomationConfig::class.java)

    fun environmentProperty(): EnvironmentConfig = ConfigFactory.create(EnvironmentConfig::class.java)

    fun desktopProperty(): DesktopConfig = ConfigFactory.create(DesktopConfig::class.java)
}
