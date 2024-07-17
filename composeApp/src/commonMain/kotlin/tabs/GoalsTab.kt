package tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import pages.GoalsPage
import pages.GoalsViewModel

object GoalsTab: Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Bolt)

            return remember {
                TabOptions(
                    index = 2u,
                    title = "Goals",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
       GoalsPage(viewModel = GoalsViewModel())
    }
}