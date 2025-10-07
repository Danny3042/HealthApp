package screens

import androidx.compose.runtime.Composable
import utils.HealthKitService

@Composable
expect fun HealthConnectScreen(healthKitService: HealthKitService)

