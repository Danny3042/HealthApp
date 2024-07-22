package pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines. flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.IGoalsStorage

class GoalsViewModel(private val goalsStorage: IGoalsStorage) : ViewModel() {
    private val _goals = MutableStateFlow(Goals(0, 0))
    val goals: StateFlow<Goals?> = _goals.asStateFlow()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            _goals.value = goalsStorage.loadGoals()
        }
    }

    fun saveGoals(goals: Goals) {
        viewModelScope.launch {
            goalsStorage.saveGoals(goals.stepsGoal, goals.exerciseGoal)
            loadGoals()
        }
    }

    fun setGoals(stepsGoal: Int, exerciseGoal: Int) {
        val newGoals = Goals(stepsGoal, exerciseGoal)
        saveGoals(newGoals)
    }
}

data class Goals(val stepsGoal: Int, val exerciseGoal: Int)

@Composable
fun GoalsPage(viewModel: GoalsViewModel) {
    var stepsGoal by remember { mutableStateOf(0) }
    var exerciseGoal by remember { mutableStateOf(0) }
    val currentGoals by viewModel.goals.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Set Your Daily Goals",
            style = MaterialTheme.typography.headlineMedium
        )

        Stepper(
            title = "Steps Goal:",
            value = stepsGoal,
            onIncrement = { stepsGoal += 500 },
            onDecrement = { if (stepsGoal >= 500) stepsGoal -= 500 }
        )

        Stepper(
            title = "Exercise Goal (minutes):",
            value = exerciseGoal,
            onIncrement = { exerciseGoal += 10 },
            onDecrement = { if (exerciseGoal >= 10) exerciseGoal -= 10 }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    viewModel.setGoals(0, 0)
                    stepsGoal = 0
                    exerciseGoal = 0
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            ) {
                Text("Clear Goals")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    viewModel.setGoals(stepsGoal, exerciseGoal)
                }
            ) {
                Text("Set Goals")
            }
        }

        Text(
            text = "Current Goals:",
            style = MaterialTheme.typography.titleMedium
        )

        GoalsCard(label = "Steps:", value = "${currentGoals?.stepsGoal ?: 0}")
        GoalsCard(label = "Exercise:", value = "${currentGoals?.exerciseGoal ?: 0} minutes")
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

@Composable
fun Stepper(title: String, value: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onDecrement) {
                    Text("-")
                }
                Text(
                    text = value.toString(),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Button(onClick = onIncrement) {
                    Text("+")
                }
            }
        }
    }
}