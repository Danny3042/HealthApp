package utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.pow

@Composable
actual fun HealthDataView(healthKitService: HealthKitService) {
    val scope = rememberCoroutineScope()
    val healthData = healthKitService.readData().collectAsState(initial = null)

    scope.launch {
        healthKitService.requestAuthorization()
    }

    MaterialTheme {
        Column {
            healthData.value?.let { data ->
                HealthDataItem(label = "Steps", value = "${data.stepCount ?: 0}", duration = "Today")
                HealthDataItem(label = "Sleep Duration", value = formatDuration(data.sleepDurationMinutes ?: 0), duration = "Last Session")
                HealthDataItem(label = "Exercise Duration (minutes)", value = "${data.exerciseDurationMinutes ?: 0}", duration = "Today")
                HealthDataItem(label = "Distance (meters)", value = roundToThreeSigFigs((data.exerciseDurationMinutes ?: 0).toDouble()), duration = "Today")
            } ?: run {
                Text(text = "No health data available", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

fun formatDuration(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return "$hours hours $minutes minutes"
}

fun roundToThreeSigFigs(number: Double): String {
    if (number == 0.0) return "0"
    val magnitude = kotlin.math.floor(kotlin.math.log10(number.absoluteValue)).toInt()
    val scale = 10.0.pow(2 - magnitude)
    val scaledAndRoundedNumber = kotlin.math.round(number * scale) / scale
    return scaledAndRoundedNumber.toString()
}

@Composable
fun HealthDataItem(label: String, value: String, duration: String) {
    val isDarkTheme = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor = if (isDarkTheme) Color.DarkGray else colorScheme.background
    val textColor = if (isDarkTheme) Color.White else colorScheme.onBackground
    val secondaryTextColor = if (isDarkTheme) Color.LightGray else colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, bottom = 8.dp, start = 4.dp, end = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                color = textColor,
                fontSize = 16.sp
            )
            HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = textColor)
            Text(
                text = duration,
                fontSize = 12.sp,
                color = secondaryTextColor
            )
            Text(
                text = value,
                fontSize = 22.sp,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                color = textColor,
                modifier = Modifier.wrapContentWidth(Alignment.Start, true)
            )
        }
    }
}