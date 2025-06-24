package pages


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import service.GenerativeAiService

@Composable
fun StressManagementPage() {
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
        Text("Stress Management", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Learn effective techniques to manage stress, improve your well-being, and maintain a balanced lifestyle.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Personalized Tip
        Text(
            "Tip: Take a few deep breaths and focus on the present moment to reduce stress.",
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
                    aiTip = GenerativeAiService.instance.getSuggestions(listOf("stress management"))
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

        // Motivational Quote
        Text(
            "\"It's not the load that breaks you down, it's the way you carry it.\"",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        // Add more stress management tools or trackers here if needed
    }
}