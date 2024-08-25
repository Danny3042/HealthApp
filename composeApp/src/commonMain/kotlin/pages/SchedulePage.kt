package pages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import model.HealthStateHolder


// src/commonMain/kotlin/pages/SchedulePage.kt

@Composable
fun SchedulePage(onNavigateToTimerView: () -> Unit) {
   val healthStateHolder = remember { HealthStateHolder() }

   LazyColumn {
      item { DescriptionCard() }
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