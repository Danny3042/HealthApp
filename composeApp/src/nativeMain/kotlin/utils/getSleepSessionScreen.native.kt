package utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import pages.SleepSessionScreen

actual fun getSleepSessionScreen(navController: NavController): @Composable () -> Unit {

    return { SleepSessionScreen(navController)}
}