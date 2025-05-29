
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.AdviceTypeDialog
import components.CalendarDataSource
import components.CompletedHabitsCard
import components.Content
import components.Header
import components.InfoCard
import components.RatingsDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import pages.SuggestionsCard
import utils.getGeminiSuggestions

@Composable
fun InsightsPage() {
    val dataSource = remember { CalendarDataSource() }
    ScheduleView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        dataSource = dataSource
    )
}

@Composable
fun ScheduleView(modifier: Modifier = Modifier, dataSource: CalendarDataSource) {
    var calendarUiModel by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }
    var showAdviceDialog by remember { mutableStateOf(false) }
    var showInfoCard by remember { mutableStateOf(true) }
    var selectedDate by remember { mutableStateOf(calendarUiModel.selectedDate.date) }
    var suggestions by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    var isFabVisible by remember { mutableStateOf(true) }
    var adviceType by remember { mutableStateOf("General") }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var showRatingsDialog by remember { mutableStateOf(false) }
    var tempSleepRating by remember { mutableStateOf(0) }
    var tempMoodRating by remember { mutableStateOf(0) }
    var showInfoCardAnim by remember { mutableStateOf(false) }
    var showSuggestionsCardAnim by remember { mutableStateOf(false) }
    var showCompletedHabitsCardAnim by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState.value) {
        isFabVisible = scrollState.value == 0
    }

    LaunchedEffect(Unit) {
        showInfoCardAnim = true
        delay(150)
        showSuggestionsCardAnim = true
        delay(150)
        showCompletedHabitsCardAnim = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = showInfoCard && showInfoCardAnim,
                enter = slideInVertically(initialOffsetY = { -40 }, animationSpec = tween(500)) + fadeIn()
            ) {
                InfoCard(onDismiss = { showInfoCard = false })
            }
            Header(
                data = calendarUiModel,
                onPrevClickListener = { startDate ->
                    calendarUiModel = dataSource.getData(
                        startDate.minus(7, DateTimeUnit.DAY),
                        calendarUiModel.selectedDate.date
                    )
                },
                onNextClickListener = { endDate ->
                    calendarUiModel = dataSource.getData(
                        endDate.plus(7, DateTimeUnit.DAY),
                        calendarUiModel.selectedDate.date
                    )
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
                onClick = { showAdviceDialog = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Get Tips")
            }
            AnimatedVisibility(
                visible = showSuggestionsCardAnim,
                enter = slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(500, delayMillis = 100)) + fadeIn()
            ) {
                SuggestionsCard(suggestions, isLoading)
            }
            AnimatedVisibility(
                visible = showCompletedHabitsCardAnim,
                enter = slideInVertically(initialOffsetY = { 80 }, animationSpec = tween(500, delayMillis = 200)) + fadeIn()
            ) {
                CompletedHabitsCard()
            }
        }

        if (isFabVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 100.dp, end = 50.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        showRatingsDialog = true
                    }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Event")
                }

                if (showRatingsDialog) {
                    RatingsDialog(
                        sleepRating = tempSleepRating,
                        moodRating = tempMoodRating,
                        onSleepRatingChange = { tempSleepRating = it },
                        onMoodRatingChange = { tempMoodRating = it },
                        onSubmit = {
                            val updatedRatings = calendarUiModel.ratings.toMutableMap()
                            updatedRatings[selectedDate] = components.Rating(
                                sleepRating = tempSleepRating,
                                moodRating = tempMoodRating
                            )
                            calendarUiModel = calendarUiModel.copy(ratings = updatedRatings)
                            dataSource.saveRatings(selectedDate, tempSleepRating, tempMoodRating)
                            showRatingsDialog = false
                        },
                        onDismiss = { showRatingsDialog = false }
                    )
                }
            }
        }
    }

    if (showAdviceDialog) {
        AdviceTypeDialog(
            onSelect = { type ->
                adviceType = type
                showAdviceDialog = false
                scope.launch {
                    isLoading = true
                    val ratings = calendarUiModel.ratings[selectedDate]
                    val sleepRating = (ratings?.sleepRating ?: 0).toInt()
                    val moodRating = (ratings?.moodRating ?: 0).toInt()
                    suggestions = getGeminiSuggestions(listOf(type), sleepRating, moodRating)
                    isLoading = false
                }
            },
            onDismiss = { showAdviceDialog = false }
        )
    }
}