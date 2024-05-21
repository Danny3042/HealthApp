
import Authentication.Authentication
import Authentication.LoginScreen
import Authentication.SignUpScreen
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import pages.HeroScreen
import pages.HomePage
import pages.HomePageScreen

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(navController, startDestination = LoginScreen) {
            composable(LoginScreen) { Authentication().Login(navController) }
            composable(SignUpScreen) { Authentication().signUp(navController) }
            composable(HomePageScreen) { HomePage() }
            composable("HeroScreen") { HeroScreen() }
        }
    }
}