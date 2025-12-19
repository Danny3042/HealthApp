import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavHostController
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import platform.PlatformBridge
import tabs.ChatTab
import tabs.HabitsTab
import tabs.HomeTab
import tabs.MeditateTab
import tabs.ProfileTab
import utils.HandleBackNavigation

const val HeroScreen = "HeroScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroScreen(navController: NavHostController) {
    HandleBackNavigation(navController)
    TabNavigator(
        HomeTab,
        tabDisposable = {
            TabDisposable(
                navigator = it,
                tabs = listOf(
                    HomeTab,
                    HabitsTab(navController),
                    MeditateTab(navController),
                    ProfileTab(navController)
                )
            )
        }
    ) { tabNavigator ->
        // Observe platform requested tab reactively (no polling)
        LaunchedEffect(Unit) {
            snapshotFlow { PlatformBridge.requestedTab }
                .filterNotNull()
                .collectLatest { requested ->
                    when (requested) {
                        "HomePage", "Home" -> tabNavigator.current = HomeTab
                        "HabitCoachingPage", "Habits" -> tabNavigator.current = HabitsTab(navController)
                        "ChatScreen", "Chat" -> tabNavigator.current = ChatTab
                        "meditation", "Meditate" -> tabNavigator.current = MeditateTab(navController)
                        "profile", "Profile" -> tabNavigator.current = ProfileTab(navController)
                    }
                    // Clear the request after handling
                    PlatformBridge.requestedTab = null
                }
        }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = tabNavigator.current.options.title) })
            },
            content = {
                CurrentTab()
            },
            bottomBar = {
                NavigationBar {
                    TabNavigationItem(HomeTab)
                    TabNavigationItem(HabitsTab(navController))
                    TabNavigationItem(ChatTab)
                    TabNavigationItem(MeditateTab(navController))
                    TabNavigationItem(ProfileTab(navController))
                }
            }
        )
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val isSelected = tabNavigator.current.key == tab.key

    NavigationBarItem(
        selected = isSelected,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
        label = { Text(text = tab.options.title) }
    )
}