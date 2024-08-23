// Update ProfilePage.kt
package pages

import Authentication.LoginScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import components.SettingsListItem
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import utils.deleteUser

@Composable
fun ProfilePage(navController: NavController) {
    val auth = Firebase.auth
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val user = auth.currentUser
    val (notificationsEnabled, setNotificationsEnabled) = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("Settings", style = MaterialTheme.typography.headlineMedium)
            }
            item {
                SettingsListItem(
                    title = "Account",
                    onClick = { /* Navigate to Account settings */ },
                    leadingIcon = {
                        Icon(Icons.Outlined.Badge, contentDescription = "Account Icon")
                    }
                ) {
                    Column {
                        Button(onClick = {
                            coroutineScope.launch {
                                auth.signOut()
                                navController.navigate(LoginScreen)
                            }
                        }) {
                            Text("Sign Out")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Delete Account", color = Color.White)
                        }
                    }
                }
                Divider()
            }
            item {
                SettingsListItem(
                    title = "Notifications",
                    onClick = { /* No navigation needed */ },
                    leadingIcon = {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications Icon")
                    }
                ) {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            setNotificationsEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                Divider()
            }
            item {
                SettingsListItem(
                    title = "About",
                    onClick = { navController.navigate(AboutPageScreen) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Info, contentDescription = "About Icon")
                    }
                )
                Divider()
            }

            if (showDeleteDialog) {
                item {
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
                    Divider()
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}