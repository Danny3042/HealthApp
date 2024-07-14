package model

// Step 1: Define the ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class HealthStateHolder {
    var sleepRating by mutableStateOf(0f)
    var moodRating by mutableStateOf(0f)
    var showDialog by mutableStateOf(false)

    fun updateSleepRating(value: Float) {
        sleepRating = value
        updateDialogVisibility()
    }

    fun updateMoodRating(value: Float) {
        moodRating = value
        updateDialogVisibility()
    }

    private fun updateDialogVisibility() {
        showDialog = (sleepRating + moodRating) / 2 < 3f
    }
}

