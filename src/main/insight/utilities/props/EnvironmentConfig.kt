package insight.utilities.props

import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.Key
import org.aeonbits.owner.Config.Sources

/**
 * Environment-specific settings. The `${env}` variable is resolved by PropertyFactory
 * from the current environment (e.g. functionaltest -> props/functionaltest.properties).
 */
@Sources("classpath:props/\${env}.properties")
interface EnvironmentConfig : Config {

    @Key("api.url")
    fun apiUrl(): String

    /** User agent sent when obtaining a token; blank when the environment does not require one. */
    @Key("user.agent")
    @Config.DefaultValue("")
    fun userAgent(): String

    @Key("device.id")
    fun deviceId(): String

    @Key("device.name")
    fun deviceName(): String

    @Key("device.externalId")
    fun deviceExternalId(): String

    @Key("device.type")
    fun deviceType(): String

    @Key("device.applicationVersion")
    fun deviceApplicationVersion(): String

    @Key("device.bootVersion")
    fun deviceBootVersion(): String
}
