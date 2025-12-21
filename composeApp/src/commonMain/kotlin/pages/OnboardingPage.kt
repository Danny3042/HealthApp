package pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

const val OnboardingPageScreen = "OnboardingPageScreen"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPage(onFinish: () -> Unit) {
    val features = listOf(
        "Symptom Checker" to "Check your symptoms and get advice.",
        "Mood Tracking" to "Track your mood and mental health.",
        "Medication Reminders" to "Get reminders for your medications."
    )
    val pagerState = rememberPagerState(pageCount = { features.size }, initialPage = 0)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(state = pagerState) { page ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(features[page].first, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(12.dp))
                Text(features[page].second, style = MaterialTheme.typography.bodyLarge)
            }
        }
        Spacer(Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                TextButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }) {
                    Text("Back")
                }
            }
            Button(
                onClick = {
                    if (pagerState.currentPage < features.size - 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinish()
                    }
                }
            ) {
                Text(if (pagerState.currentPage == features.size - 1) "Get Started" else "Next")
            }
        }
    }
}