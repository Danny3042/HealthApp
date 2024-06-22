package utils

import androidx.compose.runtime.Composable

expect class HealthKitManager() {
    fun checkAuthorization(completion: @Composable (Boolean) -> Unit)
}