package utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

const val SleepSessionRoute = "SleepSession"
expect fun getSleepSessionScreen(navController: NavController): @Composable () -> Unit