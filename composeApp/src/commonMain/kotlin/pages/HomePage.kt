import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import utils.HealthConnectChecker
import utils.HealthConnectScreen
import utils.HealthKitService
import utils.isAndroid

const val HomePageScreen = "HomePage"

@Composable
fun HomePage(healthKitService: HealthKitService) {
    var hasPermissions by remember { mutableStateOf(false) }
    var steps by remember { mutableStateOf("0") }
    var sleep by remember { mutableStateOf("0h 0m") }
    var calories by remember { mutableStateOf("0") }

    LaunchedEffect(Unit) {
        if (!isAndroid()) {
            hasPermissions = healthKitService.checkPermissions()
            if (!hasPermissions) {
                hasPermissions = healthKitService.requestAuthorization()
            }
        }
        healthKitService.readData().collect { healthData ->
            steps = healthData.stepCount?.toString() ?: "0"
            sleep = healthData.sleepDurationMinutes.toString()
            calories = healthData.calories.toString() ?: "0"
        }
    }

    val healthConnectAvailability = HealthConnectChecker.checkHealthConnectAvailability()
    if (isAndroid() && !hasPermissions) {
        HealthConnectScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text("Good Morning!", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(
                    listOf(
                        SummaryCardData("Steps", steps,
                            Icons.AutoMirrored.Filled.DirectionsWalk, Color(0xFF4CAF50)),
                        SummaryCardData("Sleep", sleep, Icons.Filled.Hotel, Color(0xFF2196F3)),
                        SummaryCardData("Calories", calories, Icons.Filled.LocalFireDepartment, Color(0xFFFF9800))
                    )
                ) { card ->
                    SummaryCard(card)
                }
            }

            Spacer(Modifier.height(32.dp))
            Text("Insights", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("You slept $sleep last night! Keep up the good work.", fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Achievements", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            // Add achievement badges or cards here
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
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(data.icon, contentDescription = data.title, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(data.value, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(data.title, color = Color.White, fontSize = 14.sp)
        }
    }
}