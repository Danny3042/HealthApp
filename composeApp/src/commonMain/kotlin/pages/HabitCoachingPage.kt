
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Habit Coaching", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Get personalized tips and guidance to build better habits. Track your progress, set reminders, and receive motivational advice to stay on track.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Personalized Tips Section
        Text(
            "Tip: Start small and be consistent. Focus on one habit at a time for better results.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Gemini AI Tip Section
        Text("AI Coach", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        when {
            isLoading -> Text("Loading AI tip...")
            error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
            aiTip != null -> Text(aiTip!!, style = MaterialTheme.typography.bodyMedium)
            else -> Text("No tip available.")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            scope.launch {
                isLoading = true
                error = null
                try {
                    aiTip = GenerativeAiService.instance.getSuggestions(listOf("habit coaching"))
                } catch (e: Exception) {
                    error = e.message
                } finally {
                    isLoading = false
                }
            }
        }) {
            Text("Get AI Tip")
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Embedded Habit Tracker
        HabitTrackerPage()
        Spacer(modifier = Modifier.height(24.dp))

        // Motivational Quote
        Text(
            "\"Success is the sum of small efforts, repeated day in and day out.\"",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}