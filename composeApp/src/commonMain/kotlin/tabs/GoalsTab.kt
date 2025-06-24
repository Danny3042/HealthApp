package tabs

import GoalsPage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import utils.HealthKitService
import utils.HealthKitServiceImpl
import utils.iOSHealthKitManager

object GoalsTab: Tab {

    private lateinit var healthKitService: HealthKitService

    init {
        val healthKitManager = iOSHealthKitManager()
        healthKitService = HealthKitServiceImpl(healthKitManager)
    }
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
        GoalsPage()
    }
}
