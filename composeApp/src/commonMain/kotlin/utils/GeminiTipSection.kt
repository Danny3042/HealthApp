package utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GeminiTipSection(
    tip: String?,
    isLoading: Boolean,
    error: String?,
    onFetchTip: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("AI Coach", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        when {
            isLoading -> Text("Loading AI tip...")
            error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
            tip != null -> Text(tip, style = MaterialTheme.typography.bodyMedium)
            else -> Text("No tip available.")
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Button to fetch a new tip
        androidx.compose.material3.Button(onClick = onFetchTip) {
            Text("Get AI Tip")
        }
    }
}