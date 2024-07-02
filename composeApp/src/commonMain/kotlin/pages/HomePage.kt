package pages

import ChatScreen
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import utils.HealthConnectChecker
import utils.HealthConnectScreen
import utils.HealthDataView
import utils.HealthKitService
import utils.isAndroid

const val HomePageScreen = "HomePage"

@Composable
fun HomePage(navController: NavController, healthKitService: HealthKitService) {
    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var showSleepScreen by remember { mutableStateOf(false) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }

    val healthConnectAvailability = HealthConnectChecker.checkHealthConnectAvailability()
    if (isAndroid() && isAuthorizedResult?.getOrNull() != true) {
        HealthConnectScreen()
    } else {
        HealthDataView(healthKitService)
    }

    FloatingActionButton(onClick = {
        try {
            navController.navigate(ChatScreen)
        } catch (e: Exception) {
            println("Navigation failed: $e")
        }
    }) {
        Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "Chat")
    }
}