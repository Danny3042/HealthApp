package pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Feature (val title: String, val description: String)

const val AboutPageScreen = "About"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(navController: NavController) {


    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val features = listOf(
            Feature(
                "Made possible by Compose Multiplatform",
                "Compose multiplatform is a toolkit for building UI for both iOS and Android."
            ),
        )
        Column {
            TopAppBar(
                title = { Text(text = "About") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
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
    }
}