package pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.vitoksmile.kmp.health.HealthDataType
import com.vitoksmile.kmp.health.HealthManagerFactory
import com.vitoksmile.kmp.health.HealthRecord
import components.HealthMetricsCard
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import utils.HealthConnectChecker
import utils.WelcomeScreen
import utils.isAndroid
import kotlin.time.Duration.Companion.days

const val HomePageScreen = "HomePage"


@Composable
fun HomePage() {
    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }

    val healthConnectAvailability = HealthConnectChecker.checkHealthConnectAvailability()
    if (isAndroid() && isAuthorizedResult?.getOrNull() != true) {
        WelcomeScreen(
            healthConnectAvailability = healthConnectAvailability,
            onResumeAvailabilityCheck = {},
            lifecycleOwner = LocalLifecycleOwner.current
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val health = remember { HealthManagerFactory().createManager() }

    val readTypes = remember {
        listOf(
            HealthDataType.Steps,
        )
    }
    val writeTypes = remember {
        listOf(
            HealthDataType.Steps,
        )
    }


    val data = remember { mutableStateMapOf<HealthDataType, Result<List<HealthRecord>>>() }

    LaunchedEffect(health) {
        isAvailableResult = health.isAvailable()

        if (isAvailableResult.getOrNull() == false) return@LaunchedEffect
        isAuthorizedResult = health.isAuthorized(
            readTypes = readTypes,
            writeTypes = writeTypes,
        )
        isRevokeSupported = health.isRevokeAuthorizationSupported().getOrNull() ?: false

        if (isAvailableResult.getOrNull() == true && isAuthorizedResult?.getOrNull() == true) {
            readTypes.forEach { type ->
                data[type] = health.readData(
                    startTime = Clock.System.now().minus(1.days),
                    endTime = Clock.System.now(),
                    type = type
                )
            }
        }
    }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Hello, and welcome to HealthCompose ")

            isAvailableResult
                .onSuccess { isAvailable ->
                    println("HealthManager isAvailable=$isAvailable")
                }
                .onFailure {
                    println("HealthManager isAvailable=$it")
                }

            isAuthorizedResult
                ?.onSuccess {
                    println("HealthManager isAuthorized=$it")
                }
                ?.onFailure {
                    println("HealthManager isAuthorized=$it")
                }
            if (isAvailableResult.getOrNull() == true && isAuthorizedResult?.getOrNull() != true)
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isAuthorizedResult = health.requestAuthorization(
                                readTypes = readTypes,
                                writeTypes = writeTypes,
                            )
                        }
                    },
                ) {
                    Text("Request authorization")
                }

            if (isAvailableResult.getOrNull() == true && isRevokeSupported && isAuthorizedResult?.getOrNull() == true)
                Button(
                    onClick = {
                        coroutineScope.launch {
                            health.revokeAuthorization()
                            isAuthorizedResult = health.isAuthorized(
                                readTypes = readTypes,
                                writeTypes = writeTypes,
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("Revoke authorization")
                }

            if (isAvailableResult.getOrNull() == true && isAuthorizedResult?.getOrNull() == true) {
                Column {
                    data.forEach {  (key,value) ->
                        HealthMetricsCard(Pair(key,value))
                    }

                    Spacer(modifier = Modifier.height(64.dp))

                }
            }
        }
    }
}



