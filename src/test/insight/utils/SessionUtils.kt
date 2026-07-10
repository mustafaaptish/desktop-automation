package insight.utils

/**
 * Generic per-thread session store keyed by class, enabling parallel scenarios
 * to keep isolated state.
 */
object SessionUtils {

    private val sessionStore = ThreadLocal<MutableMap<Class<*>, Any>>()

    private fun getSessionMap(): MutableMap<Class<*>, Any> {
        if (sessionStore.get() == null) {
            sessionStore.set(mutableMapOf())
        }
        return sessionStore.get()!!
    }

    fun <T> put(key: Class<T>, value: T) {
        getSessionMap()[key] = value as Any
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: Class<T>): T? {
        return getSessionMap()[key] as? T
    }

    fun <T> remove(key: Class<T>) {
        getSessionMap().remove(key)
    }
}
