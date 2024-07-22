import Authentication.LoginScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import pages.AboutPageScreen

@Composable
fun ProfilePage(navController: NavController) {
    val auth = Firebase.auth
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val user = auth.currentUser

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                if (user != null) {
                    // Display user information
                    Text("You are signed in as: ${user.email}", style = MaterialTheme.typography.titleMedium)
                    // Add more user details here
                }
            }
            item {
                Button(onClick = {
                    coroutineScope.launch {
                        auth.signOut()
                        navController.navigate(LoginScreen)
                    }
                }) {
                    Text("Sign Out")
                }
            }
            item {
                Button(onClick = {
                    coroutineScope.launch {
                        navController.navigate(AboutPageScreen)
                    }
                }) {
                    Text("About")
                }
            }
            item {
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete Account", color = Color.White)
                }
            }

            if (showDeleteDialog) {
                item {
                    AnimatedVisibility(
                        visible = showDeleteDialog,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Confirm Account Deletion") },
                            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            deleteUser(auth, navController, snackbarHostState)
                                        }
                                        showDeleteDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Confirm", color = Color.White)
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
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

suspend fun deleteUser(auth: FirebaseAuth, navController: NavController, snackBarHostState: SnackbarHostState) {
    val user = auth.currentUser
    if (user != null) {
        try {
            user.delete()
            println("User account deleted.")
            snackBarHostState.showSnackbar("User account deleted.")
            navController.navigate(LoginScreen)
        } catch (e: Exception) {
            println("User account deletion failed: ${e.message}")
        }
    }
}
