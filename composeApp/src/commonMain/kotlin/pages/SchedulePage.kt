package pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import model.HealthStateHolder
import utils.getGeminiSuggestions

@Composable
fun SchedulePage(onNavigateToTimerView: () -> Unit) {
   val scope = rememberCoroutineScope()
   val healthStateHolder = remember { HealthStateHolder() }
   var suggestions by remember { mutableStateOf(listOf<String>()) }

   LaunchedEffect(Unit) {
      // Fetch suggestions from Gemini API
      scope.launch {
         suggestions = getGeminiSuggestions(listOf("Sleep Rating", "Mood Rating"))
      }
   }

   LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
      item { DescriptionCard() }
      item { SuggestionsCard(suggestions) }
      item {
         ExpandableCard("Sleep Rating") { value ->
            healthStateHolder.updateSleepRating(value)
         }
      }
      item {
         ExpandableCard("Mood Rating") { value ->
            healthStateHolder.updateMoodRating(value)
         }
      }
      if (healthStateHolder.showDialog) {
         item {
            AlertDialogExample(
               onDismissRequest = { healthStateHolder.showDialog = false },
               onConfirmation = {
                  println("Navigating to TimerView")
                  onNavigateToTimerView()
                  healthStateHolder.showDialog = false
               },
               dialogTitle = "Meditation Request",
               dialogText = "Based on the information we suggest to start a meditation session."
            )
         }
      }
   }
}