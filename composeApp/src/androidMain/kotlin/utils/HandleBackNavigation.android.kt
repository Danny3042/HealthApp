package utils

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
actual fun HandleBackNavigation(navController: NavController) {
    BackHandler {
        navController.navigate("HeroScreen") {
            popUpTo("HeroScreen") { inclusive = true }
        }
    }
}