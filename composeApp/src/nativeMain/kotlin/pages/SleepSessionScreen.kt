package pages

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun SleepSessionScreen(navController: NavController) {
    Text("This is the Sleep Session Screen")
    Button(onClick = { navController.popBackStack() }) {
        Text("Go back")
    }
}