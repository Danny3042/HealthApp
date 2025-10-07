package tabs
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation.NavController
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import pages.StressManagementPage

class MeditateTab(private val navController: NavController) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Spa)

            return remember {
                TabOptions(
                    index = 4u,
                    title = "Meditate",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        StressManagementPage(navController)
    }
}