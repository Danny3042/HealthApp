package utils

import android.content.Context
import android.content.SharedPreferences

actual class OnboardingStorage(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)

    actual fun isOnboardingCompleted(): Boolean =
        prefs.getBoolean("completed", false)

    actual fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("completed", completed).apply()
    }
}