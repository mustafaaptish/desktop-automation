package insight.utilities.props

import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.Key
import org.aeonbits.owner.Config.Sources

/**
 * Global automation settings, loaded from automation.properties.
 * Values can be overridden at runtime through system properties (see AutomationUtils).
 */
@Sources("classpath:props/automation.properties")
interface AutomationConfig : Config {

    @Key("environment")
    fun environment(): String

    @Key("username")
    fun username(): String

    @Key("password")
    fun password(): String
}
