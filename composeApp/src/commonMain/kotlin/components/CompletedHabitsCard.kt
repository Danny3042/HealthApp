package components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import utils.HabitRepository

@Composable
fun CompletedHabitsCard() {
    val completedHabits = HabitRepository.completedHabits
    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Completed Habits", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (HabitRepository.completedHabits.isEmpty()) {
                Text("No habits completed yet.", modifier = Modifier.padding(top = 8.dp))
            } else {
                HabitRepository.completedHabits.forEach { habit ->
                    Text("- $habit", modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}