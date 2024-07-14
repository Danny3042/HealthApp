package tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import pages.CombinedView

object ScheduleTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.MonitorHeart)

            return remember {
                TabOptions(
                    index = 2u,
                    title = "Schedule",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        CombinedView()
    }
}