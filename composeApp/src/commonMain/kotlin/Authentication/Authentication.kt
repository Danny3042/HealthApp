package Authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import pages.HeroScreen
import pages.HomePageScreen

const val SignUpScreen = "SignUp"
const val LoginScreen = "Login"

class Authentication {

    @Composable
    fun Login(navController: NavController) {
        val scope = rememberCoroutineScope()
        val auth = remember { Firebase.auth }
        var firebaseUser: FirebaseUser? by remember { mutableStateOf(auth.currentUser) }
        var userEmail by remember { mutableStateOf("") }
        var userPassword by remember { mutableStateOf("") }

        var isPasswordIncorrect by remember { mutableStateOf(false) }

        var showSnackbar by remember { mutableStateOf(false) }
        var snackbarMessage by remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize()) {
            if (showSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("Dismiss")
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(snackbarMessage)
                }
            }

            if (firebaseUser != null) {
                navController.navigate(HomePageScreen)
            }

            if (firebaseUser == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        placeholder = { Text("Email address") },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = userPassword,
                        onValueChange = { userPassword = it },
                        placeholder = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = if (isPasswordIncorrect) TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Red
                        ) else TextFieldDefaults.textFieldColors()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    if (errorMessage != null) {
                        Text(errorMessage!!)
                    }
                    Button(onClick = {
                        scope.launch {
                            try {
                                val result = auth.signInWithEmailAndPassword(
                                    email = userEmail,
                                    password = userPassword
                                )
                                firebaseUser = result.user
                            } catch (e: FirebaseAuthInvalidUserException) {
                                snackbarMessage =
                                    "No account found with this email. Please sign up."
                                showSnackbar = true
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                snackbarMessage = "Invalid password, please try again"
                                showSnackbar = true
                            } catch (e: Exception) {
                                snackbarMessage = "An error occurred: ${e.message}"
                                showSnackbar = true
                            }
                        }
                    }) {
                        Text("Sign in")
                    }
                    OutlinedButton(onClick = {
                        navController.navigate(SignUpScreen)
                    }) {
                        Text("Sign Up")
                    }
                }
            }
            if (firebaseUser != null) {
                navController.navigate(HeroScreen)
            }
        }
    }

        @Composable
        fun signUp(navController: NavController) {
            val scope = rememberCoroutineScope()
            val auth = remember { Firebase.auth }
            var firebaseUser: FirebaseUser? by remember { mutableStateOf(null) }
            var userEmail by remember { mutableStateOf("") }
            var userPassword by remember { mutableStateOf("") }

            if (firebaseUser == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        placeholder = { Text("Email address") },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = userPassword,
                        onValueChange = { userPassword = it },
                        placeholder = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        scope.launch {
                            try {
                                val result = auth.createUserWithEmailAndPassword(
                                    email = userEmail,
                                    password = userPassword
                                )
                                firebaseUser = result.user
                            } catch (e: Exception) {
                                val result = auth.signInWithEmailAndPassword(
                                    email = userEmail,
                                    password = userPassword
                                )
                                firebaseUser = result.user
                            }
                        }
                    }) {
                        Text("Sign up")
                    }
                    OutlinedButton(onClick = { navController.navigate(LoginScreen) }) {
                        Text("Login")
                    }
                }
            }
            if (firebaseUser != null) {
                navController.navigate(HeroScreen)
            }
        }
    }