package utils
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

object SettingsManager {
    private val settings = Settings()

    private const val DARK_MODE_KEY = "dark_mode"
    private const val SYSTEM_DEFAULT_KEY = "system_default"

    fun saveDarkMode(isDark: Boolean) {
        settings[DARK_MODE_KEY] = isDark
    }

    fun loadDarkMode(): Boolean = settings.getBoolean(DARK_MODE_KEY, false)

    fun saveUseSystemDefault(useSystem: Boolean) {
        settings[SYSTEM_DEFAULT_KEY] = useSystem
    }

    fun loadUseSystemDefault(): Boolean = settings.getBoolean(SYSTEM_DEFAULT_KEY, true)
}