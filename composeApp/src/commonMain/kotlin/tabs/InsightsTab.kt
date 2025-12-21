package tabs

import pages.InsightsPage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation.compose.rememberNavController
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object InsightsTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Insights)

            return remember {
                TabOptions(
                    index = 4u,
                    title = "Insights",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val navController = rememberNavController()
        InsightsPage()
    }
}