package sub_pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import utils.HabitRepository

const val CompletedHabitsPageRoute = "completed_habits"
@Composable
fun CompletedHabitsPage() {
    val completedHabits = HabitRepository.completedHabits

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Completed Habits", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        completedHabits.forEach { habit ->
            Text(habit, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}