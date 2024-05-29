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

@Composable
fun HomePage() {

    val features = listOf(
        Feature("Coming Soon", "Exciting things to come "),
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



