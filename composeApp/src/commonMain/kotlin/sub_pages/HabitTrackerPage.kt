package sub_pages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun HabitTrackerPage(
    habits: List<String>,
    onHabitCompleted: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Habit Tracker", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        habits.forEach { habit ->
            var checked by remember { mutableStateOf(false) }
            val strikeAlpha by animateFloatAsState(
                targetValue = if (checked) 1f else 0f,
                label = "StrikethroughAlpha"
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { isChecked ->
                        checked = isChecked
                        if (isChecked) onHabitCompleted(habit)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    habit,
                    style = MaterialTheme.typography.bodyMedium.merge(
                        TextStyle(
                            textDecoration = if (checked) TextDecoration.LineThrough else null
                        )
                    ),
                    modifier = Modifier.alpha(1f - 0.5f * strikeAlpha)
                )
            }
        }
    }
}