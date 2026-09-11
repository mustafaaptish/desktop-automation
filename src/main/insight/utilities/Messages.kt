package insight.utilities

/**
 * Loads long, user-facing message text from `resources/messages/<key>.txt` instead of embedding it
 * in code, so wording and troubleshooting steps can be edited without touching Kotlin.
 *
 * Templates use `{placeholder}` markers, filled in by the values passed to [of].
 */
object Messages {

    private const val MESSAGE_PATH = "messages"

    /**
     * Renders the template stored under [key]. Placeholders with no matching value are left as-is,
     * and a missing template degrades to a readable summary rather than masking the real failure.
     */
    fun of(key: String, values: Map<String, Any?> = emptyMap()): String {
        val template = load(key) ?: return fallback(key, values)
        return values.entries.fold(template) { text, (name, value) ->
            text.replace("{$name}", value?.toString() ?: "")
        }
    }

    fun of(key: String, vararg values: Pair<String, Any?>): String = of(key, values.toMap())

    private fun load(key: String): String? =
        Messages::class.java.classLoader
            .getResourceAsStream("$MESSAGE_PATH/$key.txt")
            ?.use { it.readBytes().toString(Charsets.UTF_8) }
            ?.trimEnd()

    private fun fallback(key: String, values: Map<String, Any?>): String =
        "$key (message template $MESSAGE_PATH/$key.txt is missing)" +
            values.entries.joinToString("") { "\n  ${it.key}: ${it.value}" }
}
