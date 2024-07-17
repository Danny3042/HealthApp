package pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoalsViewModel {
    private val _goals = MutableStateFlow(Goals(0, 0))
    val goals: StateFlow<Goals> = _goals.asStateFlow()

    fun setGoals(stepsGoal: Int, exerciseGoal: Int) {
        _goals.value = Goals(stepsGoal, exerciseGoal)
    }
}

data class Goals(val stepsGoal: Int, val exerciseGoal: Int)

@Composable
fun GoalsPage(viewModel: GoalsViewModel) {
    var stepsGoal by remember { mutableStateOf("") }
    var exerciseGoal by remember { mutableStateOf("") }
    val currentGoals by viewModel.goals.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Set Your Daily Goals",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = stepsGoal,
            onValueChange = { stepsGoal = it },
            label = { Text("Daily Steps Goal") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = exerciseGoal,
            onValueChange = { exerciseGoal = it },
            label = { Text("Daily Exercise Goal (minutes)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    viewModel.setGoals(0, 0)
                    stepsGoal = "0"
                    exerciseGoal = "0"
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            ) {
                Text("Clear Goals")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val steps = stepsGoal.toIntOrNull() ?: 0
                    val exercise = exerciseGoal.toIntOrNull() ?: 0
                    viewModel.setGoals(steps, exercise)
                }
            ) {
                Text("Set Goals")
            }
        }

        Text(
            text = "Current Goals:",
            style = MaterialTheme.typography.titleMedium
        )

        GoalsCard(label = "Steps:", value = "${currentGoals.stepsGoal}")
        GoalsCard(label = "Exercise:", value = "${currentGoals.exerciseGoal} minutes")
    }
}

@Composable
fun GoalsCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}