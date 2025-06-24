package screens

import Health.HealthConnectUtils
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import kotlinx.coroutines.launch
import utils.HealthKitService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun HealthConnectScreen(healthKitService: HealthKitService) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val interval: Long = 7

    var steps by remember { mutableStateOf("0") }
    var mins by remember { mutableStateOf("0") }
    var sleepDuration by remember { mutableStateOf("00:00") }
    var showHealthConnectInstallPopup by remember { mutableStateOf(false) }

    val requestPermissions =
        rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
            if (granted.containsAll(HealthConnectUtils.PERMISSIONS)) {
                scope.launch {
                    steps = HealthConnectUtils.readStepsForInterval(interval).last().metricValue
                    sleepDuration = HealthConnectUtils.readSleepSessionsForInterval(interval).last().metricValue
                    mins = HealthConnectUtils.readMinsForInterval(interval).last().metricValue
                }
            } else {
                Toast.makeText(context, "Permissions are rejected", Toast.LENGTH_SHORT).show()
            }
        }

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
                showHealthConnectInstallPopup = true
            }
            HealthConnectClient.SDK_AVAILABLE -> {
                if (HealthConnectUtils.checkPermissions()) {
                    steps = HealthConnectUtils.readStepsForInterval(interval)[0].metricValue
                    sleepDuration = HealthConnectUtils.readSleepSessionsForInterval(interval).last().metricValue
                    mins = HealthConnectUtils.readMinsForInterval(interval).last().metricValue
                } else {
                    requestPermissions.launch(HealthConnectUtils.PERMISSIONS)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Connect", fontSize = 32.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Gray),
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
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
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(
                                listOf(
                                    SummaryCardData("Steps", steps,
                                        Icons.AutoMirrored.Filled.DirectionsWalk, Color(0xFF4CAF50)),
                                    SummaryCardData("Sleep", formatDuration(sleepDuration), Icons.Filled.Hotel, Color(0xFF2196F3)),
                                    SummaryCardData("Active min", mins, Icons.Filled.FitnessCenter, Color(0xFFFF9800))
                                )
                            ) { card ->
                                SummaryCard(card)
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                        Text("Insights", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("You slept $sleepDuration last night! Keep up the good work.", fontSize = 16.sp)
                            }
                        }
                    }

                    if (showHealthConnectInstallPopup) {
                        AlertDialog(
                            onDismissRequest = { showHealthConnectInstallPopup = false },
                            confirmButton = {
                                TextButton(onClick = {
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
                                }) {
                                    Text("Install")
                                }
                            },
                            title = {
                                Text(text = "Alert")
                            },
                            text = {
                                Text(text = "Health Connect is not installed")
                            }
                        )
                    }
                }
            }
        }
    }
}

data class SummaryCardData(
    val title: String,
    val value: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@Composable
fun SummaryCard(data: SummaryCardData) {
    Card(
        colors = CardDefaults.cardColors(containerColor = data.color),
        modifier = Modifier
            .size(width = 140.dp, height = 100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(data.icon, contentDescription = data.title, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(data.value, color = Color.White, style = MaterialTheme.typography.titleLarge)
            Text(data.title, color = Color.White, fontSize = 14.sp)
        }
    }
}

fun formatDuration(totalMinutes: String): String {
    val total = totalMinutes.toIntOrNull() ?: 0
    val hours = total / 60
    val mins = total % 60
    return "${hours}h ${mins}m"
}