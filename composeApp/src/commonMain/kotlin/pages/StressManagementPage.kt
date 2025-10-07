package pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import service.GenerativeAiService
import sub_pages.MEDITATION_PAGE_ROUTE

const val STRESS_MANAGEMENT_PAGE_ROUTE = "stress_management"
@Composable
fun StressManagementPage(navController: NavController) {
    val scope = rememberCoroutineScope()
    var aiTip by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var userActivity by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    fun generateTipAndAddActivity() {
        if (userActivity.isBlank()) return
        if (userActivity.trim().equals("meditate", ignoreCase = true)) {
            navController.navigate(MEDITATION_PAGE_ROUTE) // Replace with your actual MeditationPage route
            return
        }
        scope.launch {
            isLoading = true
            error = null
            try {
                aiTip = GenerativeAiService.instance.getSuggestions(
                    listOf("Give me a practical succinct tip for managing stress with: $userActivity")
                )
                userActivity = ""
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
            painter = rememberVectorPainter(Icons.Default.SelfImprovement),
            contentDescription = "Stress Management",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Stress Management", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Tip: Take deep breaths and give yourself a short break when feeling overwhelmed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userActivity,
            onValueChange = { userActivity = it },
            label = { Text("What stress relief activity do you want to try?") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = { generateTipAndAddActivity() },
                    enabled = userActivity.isNotBlank() && !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Activity"
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
            onClick = { generateTipAndAddActivity() },
            enabled = userActivity.isNotBlank() && !isLoading
        ) {
            Text("Get AI Tip & Add Activity")
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Replace with your own tracker for stress activities
        // StressTrackerPage(activities = trackedActivities, onActivityCompleted = { ... })

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "\"You can't always control what goes on outside. But you can always control what goes on inside.\"",
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}