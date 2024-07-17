package utils

import Health.HealthConnectUtils
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import kotlinx.coroutines.launch

@Composable
actual fun HealthConnectScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val interval: Long = 7

    var steps by remember {
        mutableStateOf("0")
    }
    var mins by remember {
        mutableStateOf("0")
    }
    var distance by remember {
        mutableStateOf("0")
    }
    var sleepDuration by remember {
        mutableStateOf("00:00")
    }

    var showHealthConnectInstallPopup by remember {
        mutableStateOf(false)
    }

    //permission launcher for the health connect
    val requestPermissions =
        rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
            if (granted.containsAll(HealthConnectUtils.PERMISSIONS)) {
                // Permissions successfully granted , continuing with reading the data from health connect
                scope.launch {
                    mins = HealthConnectUtils.readMinsForInterval(interval).last().metricValue
                    steps = HealthConnectUtils.readStepsForInterval(interval).last().metricValue
                    distance =
                        HealthConnectUtils.readDistanceForInterval(interval).last().metricValue
                    sleepDuration =
                        HealthConnectUtils.readSleepSessionsForInterval(interval).last().metricValue
                }
            } else {
                //permissions are rejected , redirect the users to health connect page to give permissions if the permissions page is not appearing
                Toast.makeText(context, "Permissions are rejected", Toast.LENGTH_SHORT).show()
            }
        }

    //checking for the Health connect availability in the device
    LaunchedEffect(key1 = true) {
        when (HealthConnectUtils.checkForHealthConnectInstalled(context)) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                Toast.makeText(
                    context,
                    "Health Connect client is not available for this device",
                    Toast.LENGTH_SHORT
                ).show()
            }

            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                //asking to install health connect if Health connect is supported but not installed
                showHealthConnectInstallPopup = true
            }

            HealthConnectClient.SDK_AVAILABLE -> {
                //checking for permissions since health connect is available
                if (HealthConnectUtils.checkPermissions()) {
                    //permissions are available , so continue performing actions on Health Connect
                    mins = HealthConnectUtils.readMinsForInterval(interval)[0].metricValue
                    steps = HealthConnectUtils.readStepsForInterval(interval)[0].metricValue
                    distance = HealthConnectUtils.readDistanceForInterval(interval)[0].metricValue
                    sleepDuration =
                        HealthConnectUtils.readSleepSessionsForInterval(interval).last().metricValue
                } else {
                    //asking for permissions from Health Connect since permissions are not given already
                    requestPermissions.launch(HealthConnectUtils.PERMISSIONS)
                }
            }
        }
    }

    Scaffold(topBar = {
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        DataItem(label = "Steps", value = steps, duration = "Today")
                        DataItem(label = "Active minutes", value = mins, duration = "Today")
                        DataItem(label = "Distance", value = distance, duration = "Today")
                        DataItem(label = "Sleep", value = sleepDuration, duration = "Last session")
                    }

                    if (showHealthConnectInstallPopup) {
                        AlertDialog(
                            onDismissRequest = { showHealthConnectInstallPopup = false },
                            confirmButton = {
                                ClickableText(text = AnnotatedString("Install"),
                                    onClick = {
                                        showHealthConnectInstallPopup = false
                                        val uriString =
                                            "market://details?id=com.google.android.apps.healthdata&url=healthconnect%3A%2F%2Fonboarding"
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW).apply {
                                                setPackage("com.android.vending")
                                                data = Uri.parse(uriString)
                                                putExtra("overlay", true)
                                                putExtra("callerId", this.`package`)
                                            }
                                        )
                                    }
                                )
                            },
                            title = {
                                Text(text = "Alert")
                            },
                            text = {
                                Text(text = "Health Connect is not installed")
                            })
                    }
                }
            }
        }
    }

}

//UI  component for displaying the metrics
@Composable
fun DataItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    duration: String,
) {
    Card(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 4.dp),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant, thickness = 1.dp)
            Text(
                text = duration,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier.wrapContentWidth(Alignment.Start, true)
            )
        }
    }
}
