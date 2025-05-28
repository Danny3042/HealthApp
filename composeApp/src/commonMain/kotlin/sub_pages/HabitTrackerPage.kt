package sub_pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp

@Composable
fun HabitTrackerPage() {
    var habits by remember { mutableStateOf(listOf("Drink Water", "Exercise", "Read")) }
    var checkedStates by remember { mutableStateOf(List(habits.size) { false }) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editingText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Habit Tracker", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        habits.forEachIndexed { index, habit ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checkedStates[index],
                    onCheckedChange = { checked ->
                        checkedStates = checkedStates.toMutableList().also { it[index] = checked }
                    }
                )
                if (editingIndex == index) {
                    BasicTextField(
                        value = editingText,
                        onValueChange = { editingText = it },
                        modifier = Modifier.padding(4.dp)
                    )
                    Button(onClick = {
                        habits = habits.toMutableList().also { it[index] = editingText }
                        editingIndex = null
                        editingText = ""
                    }) { Text("Save") }
                } else {
                    Text(habit, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            habits = habits + ""
            checkedStates = checkedStates + false
            editingIndex = habits.size // new item index
            editingText = "Edit your habit here"
        }) {
            Text("Add")
        }
    }
}