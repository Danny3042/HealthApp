package pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

const val HomePageScreen = "HomePage"
data class Feature (val title: String, val description: String)
@Composable
fun HomePage() {

    val features = listOf(
        Feature("Made possible by Compose Multiplatform", "Compose multiplatform is a toolkit for building UI for both iOS and Android."),
        Feature("Track your progress", "Track your progress with daily tasks and goals."),
        Feature("Compose yourself", "Compose yourself with the latest in UI development."),
        Feature("Thank you for joining us", "Thank you for joining us on this journey.")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(features) { feature ->
            Card(modifier = Modifier.padding(10.dp)) {
                Column {
                    Text(feature.title, modifier = Modifier.padding(10.dp))
                    Text(feature.description, modifier = Modifier.padding(10.dp))
                }
            }
        }
    }
}



