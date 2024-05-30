package navigation

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import pages.HomePageScreen

@Composable
fun WelcomePage(navController: NavController) {
    WelcomeScreen
    Button(onClick = { navController.navigate(HomePageScreen)}) {
        Text("Continue")
    }

}

