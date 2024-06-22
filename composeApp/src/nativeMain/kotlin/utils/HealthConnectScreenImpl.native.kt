package utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
actual fun HealthConnectScreen() {
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
    var isAuthorized by remember { mutableStateOf(false) }
    val healthKitManager = HealthKitManager()

    healthKitManager.checkAuthorization { success ->
        isAuthorized = success
        if (success) {
            // Fetch the actual data from HealthKit
            healthKitManager.getSteps { stepsData ->
                steps = stepsData
            }
            healthKitManager.getActiveMinutes { activeMinutesData ->
                mins = activeMinutesData
            }
            healthKitManager.getDistance { distanceData ->
                distance = distanceData
            }
            healthKitManager.getSleepDuration { sleepDurationData ->
                sleepDuration = sleepDurationData
            }
        }
    }

    if (isAuthorized) {
        Scaffold(topBar = {
            Text(
                text = "Health Connect",
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray, RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp))
                    .padding(vertical = 16.dp, horizontal = 10.dp)
            )
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
                    }
                }
            }
        }
    } else {
        // Ask for permissions or handle the unauthorized state
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
        elevation = 5.dp,
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                color = Color.Black,
                fontSize = 16.sp
            )
            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier)
            Text(
                text = duration,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Text(
                text = value,
                fontSize = 22.sp,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier.wrapContentWidth(Alignment.Start, true)
            )
        }
    }
}