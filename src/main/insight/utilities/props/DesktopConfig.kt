package insight.utilities.props

import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.Key
import org.aeonbits.owner.Config.Sources

/**
 * Desktop/WinAppDriver-specific settings, loaded from desktop.properties.
 */
@Sources("classpath:props/desktop.properties")
interface DesktopConfig : Config {

    @Key("winappdriver.url")
    fun winAppDriverUrl(): String

    @Key("winappdriver.path")
    fun winAppDriverPath(): String

    @Key("app.notepad.path")
    fun notepadPath(): String

    @Key("app.clink.path")
    fun clinkPath(): String

    @Key("app.msteams.appId")
    fun msTeamsAppId(): String

    @Key("app.msteams.windowTitle")
    fun msTeamsWindowTitle(): String
}
