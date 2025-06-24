import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import service.GenerativeAiService
import sub_pages.HabitTrackerPage

@Composable
fun HabitCoachingPage() {
    val scope = rememberCoroutineScope()
    var aiTip by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var userHabit by remember { mutableStateOf("") }
    var trackedHabits by remember { mutableStateOf(listOf<String>()) }
    val scrollState = rememberScrollState()

    fun generateTipAndAddHabit() {
        if (userHabit.isBlank()) return
        scope.launch {
            isLoading = true
            error = null
            try {
                aiTip = GenerativeAiService.instance.getSuggestions(
                    listOf("Give me a practical tip for building the habit: $userHabit")
                )
                trackedHabits = trackedHabits + userHabit
                userHabit = ""
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = rememberVectorPainter(Icons.Default.School),
            contentDescription = "Habit Coaching",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Habit Coaching", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Get personalized tips and guidance to build better habits. Track your progress, set reminders, and receive motivational advice to stay on track.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Tip: Start small and be consistent. Focus on one habit at a time for better results.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userHabit,
            onValueChange = { userHabit = it },
            label = { Text("What habit do you want to build?") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = { generateTipAndAddHabit() },
                    enabled = userHabit.isNotBlank() && !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Habit"
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("AI Coach", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                when {
                    isLoading -> Text("Loading AI tip...")
                    error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    aiTip != null -> Text(aiTip!!, style = MaterialTheme.typography.bodyMedium)
                    else -> Text("No tip available.")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { generateTipAndAddHabit() },
            enabled = userHabit.isNotBlank() && !isLoading
        ) {
            Text("Get AI Tip & Add Habit")
        }
        Spacer(modifier = Modifier.height(24.dp))

        HabitTrackerPage(habits = trackedHabits)
        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "\"Success is the sum of small efforts, repeated day in and day out.\"",
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}