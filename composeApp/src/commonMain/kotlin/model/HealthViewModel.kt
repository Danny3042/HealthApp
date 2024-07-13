package model

// Step 1: Define the ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HealthViewModel : ViewModel() {
    var sleepRating by mutableStateOf(0f)
    var moodRating by mutableStateOf(0f)
    var showDialog by mutableStateOf(false)

    private val healthRatings = listOf(
        HealthRating(0, 5f, 5f), // Sunday
        HealthRating(1, 6f, 4f), // Monday, etc.
        HealthRating(2, 7f, 3f),
        HealthRating(3, 8f, 2f),
        HealthRating(4, 9f, 1f),
        HealthRating(5, 10f, 5f),
        HealthRating(6, 5f, 4f)
        // Add entries for each day of the week
    )

    var currentRatings: HealthRating by mutableStateOf(healthRatings.first())
        private set

    fun updateCurrentDay(day: Int) {
        currentRatings = healthRatings.firstOrNull { it.dayOfWeek == day } ?: healthRatings.first()
    }

    private fun updateDialogVisibility() {
        val average = (sleepRating + moodRating) / 2
        showDialog = average < 3f // Show dialog if average rating is less than 3
    }

    fun updateSleepRating(newRating: Float) {
        sleepRating = newRating
        updateDialogVisibility()
    }

    fun updateMoodRating(newRating: Float) {
        moodRating = newRating
        updateDialogVisibility()
    }

    fun saveRating() {
        println("Saving ratings : Sleep = $sleepRating, Mood = $moodRating")
    }
}

