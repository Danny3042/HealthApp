package utils

expect object SettingsManager {
    suspend fun saveDarkMode(enabled: Boolean)
    suspend fun loadDarkMode(): Boolean

    suspend fun saveUseSystemDefault(enabled: Boolean)
    suspend fun loadUseSystemDefault(): Boolean
}