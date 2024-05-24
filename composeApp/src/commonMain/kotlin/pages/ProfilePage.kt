package pages


import Authentication.LoginScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun ProfilePage(navController: NavController) {
    val auth = Firebase.auth
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Profile Page")
        Button(onClick = {
            coroutineScope.launch {
                auth.signOut()
                navController.navigate(LoginScreen)
            }
        }) {
            Text("Sign Out")
        }
        Button(
            onClick = { showDeleteDialog = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text("Delete Account", color = Color.White)
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirm Account Deletion") },
                text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
                confirmButton = {
                    Button(onClick = {
                        coroutineScope.launch {
                            deleteUser(auth, navController)
                        }
                        showDeleteDialog = false
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteDialog = false }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}

suspend fun deleteUser(auth: FirebaseAuth, navController: NavController) {
    val user = auth.currentUser
    if (user != null) {
        try {
            user.delete()
            println("User account deleted.")
            navController.navigate(LoginScreen)
        } catch (e: Exception) {
            println("User account deletion failed: ${e.message}")
        }
    }
}