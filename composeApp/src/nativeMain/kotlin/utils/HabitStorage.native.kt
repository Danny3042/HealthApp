package utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

actual object HabitStorage {
    private val defaults = NSUserDefaults.standardUserDefaults
    private const val KEY = "completed_habits"

    actual fun saveHabits(habits: List<String>) {
        defaults.setObject(Json.encodeToString(habits), forKey = KEY)
    }

    actual fun loadHabits(): List<String> {
        val json = defaults.stringForKey(KEY) ?: return emptyList()
        return try { Json.decodeFromString(json) } catch (_: Exception) { emptyList() }
    }
}