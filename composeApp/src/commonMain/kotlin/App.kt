
import Authentication.Authentication
import Authentication.LoginScreen
import Authentication.ResetPasswordScreen
import Authentication.SignUpScreen
import Colors.DarkColors
import Colors.LightColors
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import pages.AboutPage
import pages.AboutPageScreen
import pages.HomePage
import pages.HomePageScreen
import tabs.ProfileTab
import utils.HealthKitServiceImpl
import utils.iOSHealthKitManager


@Composable
@Preview
fun App() {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors

    MaterialTheme(colorScheme = colors) {
        val navController = rememberNavController()
        val healthKitManager = iOSHealthKitManager()
        val healthKitService = HealthKitServiceImpl(healthKitManager)
        NavHost(navController, startDestination = LoginScreen) {
            composable(LoginScreen) { Authentication().Login(navController) }
            composable(SignUpScreen) { Authentication().signUp(navController) }
            composable(ResetPasswordScreen) { Authentication().ResetPassword(navController)}
            composable(HomePageScreen) { HomePage(healthKitService) }
            composable(HeroScreen) { HeroScreen(navController) }
            composable("profile") { ProfileTab(navController).Content() }
            composable(AboutPageScreen) { AboutPage(navController) }
        }
    }
}