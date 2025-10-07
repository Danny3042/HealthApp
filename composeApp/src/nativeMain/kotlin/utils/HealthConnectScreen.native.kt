package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import utils.HealthKitService

@Composable
actual fun HealthConnectScreen(healthKitService: HealthKitService) {
    val healthData = healthKitService.readData().collectAsState(initial = null)

    LaunchedEffect(Unit) {
        healthKitService.requestAuthorization()
    }

    MaterialTheme {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Good Morning!", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(
                    listOf(
                        SummaryCardData("Steps", healthData.value?.stepCount?.toString() ?: "0",
                            Icons.AutoMirrored.Filled.DirectionsWalk, Color(0xFF4CAF50)),
                        SummaryCardData(
                            "Sleep",
                            formatDuration(healthData.value?.sleepDurationMinutes),
                            Icons.Filled.Hotel,
                            Color(0xFF2196F3)
                        ),
                        SummaryCardData("Calories Burned", healthData.value?.calories?.toString() ?: "0", Icons.Filled.LocalFireDepartment, Color(0xFF9C27B0))
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
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("You slept ${healthData.value?.sleepDurationMinutes ?: 0} minutes last night! Keep up the good work.", fontSize = 16.sp)
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
            androidx.compose.material3.Icon(data.icon, contentDescription = data.title, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(data.value, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(data.title, color = Color.White, fontSize = 14.sp)
        }
    }
}

fun formatDuration(totalMinutes: Int?): String {
    val total = totalMinutes ?: 0
    val hours = total / 60
    val minutes = total % 60
    return "${hours}h ${minutes}m"
}