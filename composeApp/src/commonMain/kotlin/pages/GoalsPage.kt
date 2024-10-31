package pages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.IGoalsStorage
import utils.PlatformContext
import utils.StepCounter

class GoalsViewModel(private val goalsStorage: IGoalsStorage) : ViewModel() {
    private val _goals = MutableStateFlow(Goals(0, 0))
    val goals: StateFlow<Goals?> = _goals.asStateFlow()

    // Add progress state flows
    private val _stepsProgress = MutableStateFlow(0)
    val stepsProgress: StateFlow<Int> = _stepsProgress.asStateFlow()

    private val _exerciseProgress = MutableStateFlow(0)
    val exerciseProgress: StateFlow<Int> = _exerciseProgress.asStateFlow()

    private val _goalAchieved = MutableStateFlow(false)
    val goalAchieved: StateFlow<Boolean> = _goalAchieved.asStateFlow()

    private lateinit var stepCounter: StepCounter

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            _goals.value = goalsStorage.loadGoals()
            // Load progress along with goals
            _stepsProgress.value = goalsStorage.loadStepsProgress()
            _exerciseProgress.value = goalsStorage.loadExerciseProgress()
        }
    }

    fun saveGoals(goals: Goals) {
        viewModelScope.launch {
            goalsStorage.saveGoals(goals.stepsGoal, goals.exerciseGoal)
            // Save progress along with goals
            goalsStorage.saveStepsProgress(_stepsProgress.value)
            goalsStorage.saveExerciseProgress(_exerciseProgress.value)
            loadGoals()
        }
    }

    // Methods to update progress
    fun updateStepsProgress(progress: Int) {
        viewModelScope.launch {
            _stepsProgress.value = progress
            // Optionally save progress immediately
            goalsStorage.saveStepsProgress(progress)
        }
    }

    fun updateExerciseProgress(progress: Int) {
        viewModelScope.launch {
            _exerciseProgress.value = progress
            // Optionally save progress immediately
            goalsStorage.saveExerciseProgress(progress)
        }
    }

    fun setGoals(stepsGoal: Int, exerciseGoal: Int, context: PlatformContext) {
        viewModelScope.launch {
            _goals.value = Goals(stepsGoal, exerciseGoal)
            saveGoals(_goals.value)
            startStepCounter(context)
        }
    }

    fun startStepCounter(context: PlatformContext) {
        stepCounter = StepCounter(context)
        stepCounter.startListening(_goals.value.stepsGoal) { steps: Int ->
            _stepsProgress.value = steps
            if (steps >= _goals.value.stepsGoal) {
                _goalAchieved.value = true
            }
        }
    }

    fun stopStepCounter() {
        stepCounter.stopListening()
    }
}

data class Goals(val stepsGoal: Int, val exerciseGoal: Int)

@Composable
fun GoalsPage(viewModel: GoalsViewModel, context: PlatformContext) {
    var stepsGoal by remember { mutableStateOf(0) }
    var exerciseGoal by remember { mutableStateOf(0) }
    val currentGoals by viewModel.goals.collectAsState()
    val stepsProgress by viewModel.stepsProgress.collectAsState()
    val exerciseProgress by viewModel.exerciseProgress.collectAsState()
    val goalAchieved by viewModel.goalAchieved.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startStepCounter(context)
    }

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProgressBarCard("Steps Progress", stepsProgress.toFloat() / (currentGoals?.stepsGoal ?: 1), currentGoals?.stepsGoal ?: 0)
            ProgressBarCard("Exercise Progress", exerciseProgress.toFloat() / (currentGoals?.exerciseGoal ?: 1), currentGoals?.exerciseGoal ?: 0)
        }

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
                    viewModel.setGoals(0, 0, context)
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
                    viewModel.setGoals(stepsGoal, exerciseGoal, context)
                }
            ) {
                Text("Set Goals")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (goalAchieved) {
            Text(
                text = "Congratulations! You have achieved your step goal!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Green,
                modifier = Modifier.padding(top = 8.dp)
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

@Composable
fun CircularProgressBar(progress: Float, goalValue: Int, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(100.dp)
    ) {
        val onSurface = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        val onPrimary = MaterialTheme.colorScheme.primary
        Canvas(modifier = Modifier.size(100.dp)) {
            val strokeWidth = 8.dp.toPx()
            drawArc(
                color = onSurface,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = onPrimary,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "$goalValue",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ProgressBarCard(title: String, progress: Float, goalValue: Int) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressBar(progress = progress, goalValue = goalValue, modifier = Modifier.fillMaxWidth())
            Text(
                text = "Progress: ${progress * 100}% of $goalValue goal",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}