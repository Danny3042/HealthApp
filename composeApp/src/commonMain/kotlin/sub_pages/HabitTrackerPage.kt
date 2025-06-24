package sub_pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HabitTrackerPage(habits: List<String>) {
    var checkedStates by remember { mutableStateOf(habits.associateWith { false }) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Habit Tracker", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        habits.forEach { habit ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = checkedStates[habit] ?: false,
                    onCheckedChange = { isChecked ->
                        checkedStates = checkedStates.toMutableMap().apply {
                            this[habit] = isChecked
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(habit, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}