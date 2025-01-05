package pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import components.CalendarDataSource
import components.Content
import components.Header
import components.InfoCard
import components.Rating
import components.RatingInputDialog
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import utils.getGeminiSuggestions

@Composable
fun ScheduleView(modifier: Modifier = Modifier, dataSource: CalendarDataSource) {
   var calendarUiModel by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }
   var showDialog by remember { mutableStateOf(false) }
   var showInfoCard by remember { mutableStateOf(true) }
   var selectedDate by remember { mutableStateOf(calendarUiModel.selectedDate.date) }
   var suggestions by remember { mutableStateOf(listOf<String>()) }
   var isLoading by remember { mutableStateOf(false) }
   val scope = rememberCoroutineScope()

   Box(modifier = modifier.fillMaxSize()) {
      Column(
         modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 54.dp)
            .verticalScroll(rememberScrollState()) // Make the Column scrollable
      ) {
         if (showInfoCard) {
            InfoCard(onDismiss = { showInfoCard = false })
         }
         Header(
            data = calendarUiModel,
            onPrevClickListener = { startDate ->
               calendarUiModel = dataSource.getData(startDate.minus(7, DateTimeUnit.DAY), calendarUiModel.selectedDate.date)
            },
            onNextClickListener = { endDate ->
               calendarUiModel = dataSource.getData(endDate.plus(7, DateTimeUnit.DAY), calendarUiModel.selectedDate.date)
            },
            onTodayClickListener = {
               calendarUiModel = dataSource.getData(lastSelectedDate = dataSource.today)
            }
         )
         Content(
            calendarUiModel = calendarUiModel,
            selectedDate = selectedDate,
            onDateClickListener = { date ->
               selectedDate = date.date
               calendarUiModel = calendarUiModel.copy(selectedDate = date)
            },
            onUpdateCalendarUiModel = { updatedModel ->
               calendarUiModel = updatedModel
            }
         )
         Button(
            onClick = {
               scope.launch {
                  isLoading = true
                  suggestions = getGeminiSuggestions(listOf("Sleep Rating", "Mood Rating"))
                  isLoading = false
               }
            },
            modifier = Modifier.padding(16.dp)
         ) {
            Text("Get Weekly Suggestions")
         }
         SuggestionsCard(suggestions)
      }
      Box(
         modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 100.dp, end = 50.dp) // Adjust padding to place above nav bar
      ) {
         FloatingActionButton(
            onClick = {
               showDialog = true
            }
         ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Event")
         }
      }

      if (isLoading) {
         CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
         )
      }
   }

   if (showDialog) {
      RatingInputDialog(
         onConfirm = { sleepRating, moodRating ->
            val newRatings = calendarUiModel.ratings.toMutableMap()
            newRatings[calendarUiModel.selectedDate.date] = Rating(sleepRating, moodRating)
            calendarUiModel = calendarUiModel.copy(ratings = newRatings)
            showDialog = false
         },
         onDismissRequest = { showDialog = false }
      )
   }
}