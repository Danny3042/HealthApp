//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.WindowInsets
//import androidx.compose.foundation.layout.asPaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.navigationBars
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.ModalBottomSheet
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import sub_pages.HabitTrackerPage
//import pages.StressManagementPage
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun GoalsPage() {
//    val allFeatures = listOf("Meditation", "Habit Tracker", "Habit Coaching", "Stress Management")
//    var addedFeatures by remember { mutableStateOf(listOf<String>()) }
//    var showSheet by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 16.dp, vertical = 32.dp),
//        contentAlignment = Alignment.BottomCenter
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(bottom = 72.dp),
//            verticalArrangement = Arrangement.spacedBy(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text("Goals", style = MaterialTheme.typography.headlineMedium)
//            addedFeatures.forEach { feature ->
//                SectionCard(title = feature) {
//                    when (feature) {
//                        "Meditation" -> MeditationPage()
//                        "Habit Tracker" -> HabitTrackerPage()
//                        "Habit Coaching" -> HabitCoachingPage()
//                        "Stress Management" -> StressManagementPage()
//                    }
//                }
//            }
//        }
//        FloatingActionButton(
//            onClick = { showSheet = true },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(
//                    end = 16.dp,
//                    bottom = 64.dp
//                )
//                .padding(WindowInsets.navigationBars.asPaddingValues())
//        ) {
//            Icon(Icons.Default.Add, contentDescription = "Add Feature")
//        }
//        if (showSheet) {
//            ModalBottomSheet(
//                onDismissRequest = { showSheet = false }
//            ) {
//                Column(
//                    modifier = Modifier.padding(24.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("Feature Catalog", style = MaterialTheme.typography.headlineSmall)
//                    Spacer(modifier = Modifier.height(16.dp))
//                    allFeatures.filter { it !in addedFeatures }.forEach { feature ->
//                        Button(
//                            onClick = {
//                                addedFeatures = addedFeatures + feature
//                                showSheet = false
//                            },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 8.dp)
//                        ) {
//                            Text(feature)
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(16.dp))
//                    TextButton(onClick = { showSheet = false }) {
//                        Text("Close")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun SectionCard(title: String, content: @Composable () -> Unit) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(2.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//    ) {
//        Column(modifier = Modifier.padding(12.dp)) {
//            Text(title, style = MaterialTheme.typography.titleMedium)
//            Spacer(modifier = Modifier.height(8.dp))
//            content()
//        }
//    }
//}