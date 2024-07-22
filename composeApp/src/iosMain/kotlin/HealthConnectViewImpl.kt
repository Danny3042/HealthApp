
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import utils.HealthKitService

@Composable
fun HealthDataView(healthKitService: HealthKitService) {
    val scope = rememberCoroutineScope()
    val healthData = healthKitService.readData().collectAsState(initial = null)

    scope.launch {
        healthKitService.requestAuthorization()
    }

    MaterialTheme {
        Column {
            Text(text = "Health Data", color = MaterialTheme.colors.onSurface)
            healthData.value?.let { data ->
                Card {
                    Row {
                        Text(text = "Steps: ", color = MaterialTheme.colors.onSurface)
                        Text(text = "${data.stepCount}", color = MaterialTheme.colors.onSurface)
                    }
                }
                Card {
                    Row {
                        Text(text = "Sleep Duration (minutes): ", color = MaterialTheme.colors.onSurface)
                        Text(text = "${data.sleepDurationMinutes}", color = MaterialTheme.colors.onSurface)
                    }
                }
                Card {
                    Row {
                        Text(text = "Exercise Duration (minutes): ", color = MaterialTheme.colors.onSurface)
                        Text(text = "${data.exerciseDurationMinutes}", color = MaterialTheme.colors.onSurface)
                    }
                }
                Card {
                    Row {
                        Text(text = "Distance (meters): ", color = MaterialTheme.colors.onSurface)
                        Text(text = "${data.distanceMeters}", color = MaterialTheme.colors.onSurface)
                    }
                }
            }
        }
    }
}

