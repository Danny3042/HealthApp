
import Authentication.Authentication
import Authentication.LoginScreen
import Authentication.ResetPasswordScreen
import Authentication.SignUpScreen
import Colors.DarkColors
import Colors.LightColors
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import config.VERSION_NUMBER
import org.jetbrains.compose.ui.tooling.preview.Preview
import pages.HomePageScreen
import pages.Timer
import pages.TimerScreenContent
import sub_pages.AboutPage
import sub_pages.AboutPageScreen
import sub_pages.NotificationPage
import sub_pages.NotificationPageScreen
import tabs.HomeTab
import tabs.ProfileTab
import utils.HealthKitServiceImpl
import utils.SettingsManager
import utils.iOSHealthKitManager

@Composable
@Preview
fun App() {
    var isDarkMode by remember { mutableStateOf(false) }
    var useSystemDefault by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isDarkMode = SettingsManager.loadDarkMode()
        useSystemDefault = SettingsManager.loadUseSystemDefault()
    }

    LaunchedEffect(isDarkMode) { SettingsManager.saveDarkMode(isDarkMode) }
    LaunchedEffect(useSystemDefault) { SettingsManager.saveUseSystemDefault(useSystemDefault) }

    val darkMode = if (useSystemDefault) isSystemInDarkTheme() else isDarkMode
    val colors = if (darkMode) DarkColors else LightColors

    MaterialTheme(colorScheme = colors) {
        val navController = rememberNavController()
        val healthKitManager = iOSHealthKitManager()
        val healthKitService = HealthKitServiceImpl(healthKitManager)

        LaunchedEffect(Unit) {
            NotifierManager.initialize(NotificationPlatformConfiguration.Ios(
                showPushNotification = true
            ))
        }

        NavHost(navController, startDestination = LoginScreen) {
            composable(LoginScreen) { Authentication().Login(navController) }
            composable(SignUpScreen) { Authentication().signUp(navController) }
            composable(ResetPasswordScreen) { Authentication().ResetPassword(navController)}
            composable(HomePageScreen) { HomeTab.Content() }
            composable(ChatPageScreen) { ChatScreen() }
            composable(HeroScreen) { HeroScreen(navController) }
            composable("profile") { ProfileTab(navController).Content() }
            composable(AboutPageScreen) { AboutPage(navController, versionNumber = VERSION_NUMBER) }
            composable(NotificationPageScreen) { NotificationPage(navController) }
            composable(DarkModeSettingsPageScreen) {
                DarkModeSettingsPage(
                    isDarkMode = isDarkMode,
                    onDarkModeToggle = { isDarkMode = it },
                    useSystemDefault = useSystemDefault,
                    onUseSystemDefaultToggle = { useSystemDefault = it },
                    navController = navController
                )
            }
            composable(Timer) { TimerScreenContent(onBack = { navController.popBackStack() }) }
        }
    }
}