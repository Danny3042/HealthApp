package utils
import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual object HabitStorage {
    private lateinit var prefs: SharedPreferences
    private const val KEY = "completed_habits"

    fun init(context: Context) {
        prefs = context.getSharedPreferences("habits", Context.MODE_PRIVATE)
    }

    actual fun saveHabits(habits: List<String>) {
        prefs.edit().putString(KEY, Json.encodeToString(habits)).apply()
    }

    actual fun loadHabits(): List<String> {
        val json = prefs.getString(KEY, null) ?: return emptyList()
        return try { Json.decodeFromString(json) } catch (_: Exception) { emptyList() }
    }
}