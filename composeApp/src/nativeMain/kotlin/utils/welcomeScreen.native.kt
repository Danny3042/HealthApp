package utils

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner

@Composable
actual fun WelcomeScreen(
    healthConnectAvailability: Int,
    onResumeAvailabilityCheck: () -> Unit,
    lifecycleOwner: LifecycleOwner
) {

}