package pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
    var isFabVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                isFabVisible = index == 0
            }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 54.dp)
        ) {
            item {
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
                            val ratings = calendarUiModel.ratings[selectedDate]
                            val sleepRating = (ratings?.sleepRating ?: 0).toInt()
                            val moodRating = (ratings?.moodRating ?: 0).toInt()
                            suggestions = getGeminiSuggestions(listOf("Sleep Rating", "Mood Rating"), sleepRating, moodRating)
                            isLoading = false
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Get Tips")
                }
                SuggestionsCard(suggestions, isLoading)
            }
        }
        if (isFabVisible) {
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