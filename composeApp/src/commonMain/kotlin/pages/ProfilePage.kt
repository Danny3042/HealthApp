package pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun ProfilePage() {
    val auth = Firebase.auth
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Profile Page")
    }

    Button(onClick = {
        coroutineScope.launch {
            if (auth.currentUser != null) {
                println("Signing out user: ${auth.currentUser?.email}")
                auth.signOut()
                if (auth.currentUser == null) {
                    println("User signed out")
                } else {
                    println("Failed to sign out user")
                }
            } else {
                println("No user to sign out")
            }
        }
    }) {
        Text("Sign Out")
    }

}