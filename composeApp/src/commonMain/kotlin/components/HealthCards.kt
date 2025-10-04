package components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthRecord
import com.viktormykhailiv.kmp.health.records.StepsRecord


@Composable
fun HealthMetricsCard(metric: Pair<HealthDataType, Result<List<HealthRecord>>>) {
    val (type, result) = metric
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column {
            Text(
                text = type.toString(),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp)
            )
            result.onSuccess { records ->
                val stepsCount = records.filterIsInstance<StepsRecord>().sumOf { it.count }
                Text(
                    text = "Steps count: $stepsCount",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp)
                )

            }
            result.onFailure {
                Text(
                    text = "Failed to read records $it",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}