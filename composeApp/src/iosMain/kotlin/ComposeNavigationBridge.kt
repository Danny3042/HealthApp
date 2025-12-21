// ComposeNavigationBridge.kt
// Add this to your Compose Multiplatform project (iosMain)

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue

/**
 * Bridge between Compose Navigation and native iOS NavigationStack.
 * Call this from your root Composable near your NavHost.
 */
@Composable
@Suppress("unused")
fun SetupIOSNavigationBridge(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value

    // Listen for back button presses from SwiftUI
    LaunchedEffect(Unit) {
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = "ComposeBackPressed",
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            if (navController.previousBackStackEntry != null) {
                navController.popBackStack()
            }
        }
    }

    // Send navigation state updates to SwiftUI whenever navigation changes
    LaunchedEffect(navBackStackEntry) {
        val currentRoute = navBackStackEntry?.destination?.route ?: "HomePage"
        val canGoBack = navController.previousBackStackEntry != null

        // Map your routes to display titles
        val title = when {
            currentRoute.startsWith("HomePage") -> "Home"
            currentRoute.startsWith("HabitCoachingPage") -> "Habits"
            currentRoute.startsWith("ChatScreen") -> "Chat"
            currentRoute.startsWith("meditation") -> "Meditate"
            currentRoute.startsWith("profile") -> "Profile"
            currentRoute.startsWith("HabitDetail") -> "Habit Details"
            currentRoute.startsWith("Settings") -> "Settings"
            // Add more route mappings here
            else -> "Back"
        }

        NSNotificationCenter.defaultCenter.postNotificationName(
            "ComposeNavigationChanged",
            null,
            mapOf(
                "title" to title,
                "canGoBack" to canGoBack
            )
        )
    }
}

/**
 * Usage in your main Compose app:
 *
 * @Composable
 * fun App() {
 *     val navController = rememberNavController()
 *
 *     // Add this bridge setup
 *     SetupIOSNavigationBridge(navController)
 *
 *     // Hide Compose TopAppBar on iOS since we're using native navigation
 *     NavHost(
 *         navController = navController,
 *         startDestination = "HomePage"
 *     ) {
 *         composable("HomePage") {
 *             HomeScreen(
 *                 onNavigate = { route -> navController.navigate(route) }
 *                 // Don't show TopAppBar in Scaffold on iOS
 *             )
 *         }
 *         // ... other routes
 *     }
 * }
 */
