package utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import navigation.sleep.SleepSessionScreen
import navigation.sleep.SleepSessionViewModel


actual fun getSleepSessionScreen(navController: NavController): @Composable () -> Unit = {
    // retrieve the necessary data for the sleep session screen parameters
    val permissions = setOf<String>()
    val permissionsGranted = true
    val sessionsList = listOf<SleepSessionData>()
    val uiState = SleepSessionViewModel.UiState.Uninitialized
    SleepSessionScreen(
        permissions = permissions,
        permissionsGranted = permissionsGranted,
        sessionsList = sessionsList,
        uiState = uiState,
    )
}