import Authentication.Authentication
import Authentication.LoginScreen
import Authentication.ResetPasswordScreen
import Authentication.SignUpScreen
import Colors.DarkColors
import Colors.LightColors
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import config.VERSION_NUMBER
import pages.HomePageScreen
import pages.InsightsPage
import pages.InsightsPageScreen
import pages.STRESS_MANAGEMENT_PAGE_ROUTE
import pages.StressManagementPage
import pages.Timer
import pages.TimerScreenContent
import sub_pages.AboutPage
import sub_pages.AboutPageScreen
import sub_pages.CompletedHabitsPage
import sub_pages.CompletedHabitsPageRoute
import sub_pages.MEDITATION_PAGE_ROUTE
import sub_pages.MeditationPage
import sub_pages.NotificationPage
import sub_pages.NotificationPageScreen
import tabs.HomeTab
import tabs.ProfileTab
import utils.SettingsManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSDictionary
import platform.Foundation.NSOperationQueue
import platform.PlatformBridge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformApp() {
    // remember a NavController for Compose navigation on iOS
    val navController = rememberNavController()

    // pending route set by native observer; Compose will navigate when this changes
    var pendingRoute by remember { mutableStateOf<String?>(null) }

    // If a pending route is set (by the NotificationCenter callback), navigate from Compose context
    LaunchedEffect(pendingRoute) {
        val route = pendingRoute
        if (!route.isNullOrEmpty()) {
            try {
                println("PlatformIos: LaunchedEffect navigating to route: $route")
                navController.navigate(route)
            } catch (e: Throwable) {
                println("Failed to navigate from LaunchedEffect to route: $route, error: ${e.message}")
            }
            pendingRoute = null
        }
    }

    // Observe native navigation requests from AuthManager (Swift) via NotificationCenter
    DisposableEffect(navController) {
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = "AuthManagerNavigateToRoute",
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { notification: NSNotification? ->
            println("PlatformIos: Received AuthManagerNavigateToRoute notification")
            val userInfo = notification?.userInfo as? NSDictionary
            val route = (userInfo?.objectForKey("route") as? String)
            println("PlatformIos: route from notification = $route")
            if (!route.isNullOrEmpty()) {
                try {
                    // If the route looks like a tab request, set the PlatformBridge requestedTab and request navigation
                    val tabRoutes = setOf("HomePage", "HabitCoachingPage", "ChatScreen", "meditation", "profile", "Home", "Habits", "Chat", "Meditate", "Profile")
                    if (tabRoutes.contains(route)) {
                        PlatformBridge.requestedTab = route
                        pendingRoute = "HeroScreen"
                    } else {
                        pendingRoute = route
                    }
                } catch (e: Throwable) {
                    println("Failed to handle notification route: $route, error: ${e.message}")
                }
            }
        }
        onDispose {
            if (observer != null) {
                NSNotificationCenter.defaultCenter.removeObserver(observer as Any)
            }
        }
    }

    // Render the shared Compose NavHost on iOS so the Compose MainViewController shows the full app
    MaterialTheme(colorScheme = LightColors) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navControllerLocal = navController
            NavHost(navController = navControllerLocal, startDestination = LoginScreen) {
                composable(LoginScreen) { Authentication().Login(navControllerLocal) }
                composable("HeroScreen") { HeroScreen(navControllerLocal, showBottomBar = false) }
                composable(SignUpScreen) { Authentication().signUp(navControllerLocal) }
                composable(ResetPasswordScreen) { Authentication().ResetPassword(navControllerLocal) }
                composable(HomePageScreen) { HomeTab.Content() }
                composable(InsightsPageScreen) { InsightsPage() }
                composable(STRESS_MANAGEMENT_PAGE_ROUTE) { StressManagementPage(navControllerLocal) }
                composable(MEDITATION_PAGE_ROUTE) { MeditationPage(onBack = { navControllerLocal.popBackStack() }, onNavigateToInsights = { navControllerLocal.navigate(InsightsPageScreen) }) }
                composable(CompletedHabitsPageRoute) { CompletedHabitsPage(navControllerLocal) }
                composable(NotificationPageScreen) { NotificationPage(navControllerLocal) }
                composable(AboutPageScreen) { AboutPage(navControllerLocal, versionNumber = VERSION_NUMBER) }
                composable("TimerScreen") { TimerScreenContent(onBack = { navControllerLocal.popBackStack() }) }
            }
        }
    }
}
