package pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import utils.HealthConnectChecker
import utils.HealthConnectScreen
import utils.isAndroid

const val HomePageScreen = "HomePage"

@Composable
fun HomePage(navController: NavController) {
    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var showSleepScreen by remember { mutableStateOf(false) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }

    val healthConnectAvailability = HealthConnectChecker.checkHealthConnectAvailability()

    if (isAndroid() && isAuthorizedResult?.getOrNull() != true) {
        HealthConnectScreen()
    }
}

