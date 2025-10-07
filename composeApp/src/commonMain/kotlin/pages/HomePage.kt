package pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import screens.HealthConnectScreen
import utils.HealthConnectChecker
import utils.HealthKitService
import utils.isAndroid

const val HomePageScreen = "HomePage"

@Composable
fun HomePage(healthKitService: HealthKitService) {
    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var showSleepScreen by remember { mutableStateOf(false) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }
    var hasPermissions by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isAndroid()) {
            hasPermissions = healthKitService.checkPermissions()
            if (!hasPermissions) {
                hasPermissions = healthKitService.requestAuthorization()
            }
        }
    }

    val healthConnectAvailability = HealthConnectChecker.checkHealthConnectAvailability()
    if (isAndroid() && isAuthorizedResult?.getOrNull() != true) {
        HealthConnectScreen(healthKitService)
    } else {
        HealthConnectScreen(healthKitService)
    }
}