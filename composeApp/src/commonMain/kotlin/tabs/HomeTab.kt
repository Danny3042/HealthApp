package tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation.compose.rememberNavController
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import pages.HomePage
import utils.HealthKitService
import utils.HealthKitServiceImpl
import utils.iOSHealthKitManager

object HomeTab : Tab {

    private lateinit var healthKitService: HealthKitService

    init {
        val healthKitManager = iOSHealthKitManager()
        healthKitService = HealthKitServiceImpl(healthKitManager)
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 1u,
                    title = "Home",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val navController = rememberNavController()
        HomePage(healthKitService)
    }
}