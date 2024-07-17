package Authentication

import HeroScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import hideKeyboard
import kotlinx.coroutines.launch
import pages.HomePageScreen

const val SignUpScreen = "SignUp"
const val LoginScreen = "Login"

class Authentication {


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Login(navController: NavController) {
        val scope = rememberCoroutineScope()
        val auth = remember { Firebase.auth }
        var firebaseUser: FirebaseUser? by remember { mutableStateOf(auth.currentUser) }
        var userEmail by remember { mutableStateOf("") }
        var userPassword by remember { mutableStateOf("") }

        var isPasswordIncorrect by remember { mutableStateOf(false) }
        var isPasswordVisible by remember { mutableStateOf(false) }

        var showSnackbar by remember { mutableStateOf(false) }
        var snackbarMessage by remember { mutableStateOf("") }

        var authready by remember { mutableStateOf(false) }
        var onFirebaseResult: (Result<FirebaseUser?>) -> Unit = { result ->
            if (result.isSuccess) {
                val user = result.getOrNull()
                println("User: $user")
            } else {
                val error = result.exceptionOrNull()
                println("Error Result: ${result.exceptionOrNull()?.message}")
            }
        }

        LaunchedEffect(Unit) {
            GoogleAuthProvider.create(
                credentials = GoogleAuthCredentials(
                    serverId = "991501394909-ij12drqd040b9766t2t9s5d0itjs46h3.apps.googleusercontent.com"
                )
            )
            authready = true
        }

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (snackbarMessage.isNotEmpty()) {
                Text(snackbarMessage, color = MaterialTheme.colorScheme.error)
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
                        label = { Text("Email address") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                           hideKeyboard()
                        }),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.onSurface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = userPassword,
                        onValueChange = { userPassword = it },
                        placeholder = { Text("Password") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            hideKeyboard()
                        }),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(image, contentDescription = "Toggle password visibility")
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.onSurface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    if (errorMessage != null) {
                        Text(errorMessage!!)
                    }
                    Button(onClick = {
                        hideKeyboard()
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
                    if (authready) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            GoogleButtonUiContainerFirebase(onResult = onFirebaseResult) {
                                GoogleSignInButton(fontSize = 19.sp) { this.onClick() }
                            }
                        }
                    }
                }
            }
            if (firebaseUser != null) {
                navController.navigate(HeroScreen)
            }
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun signUp(navController: NavController) {
        val scope = rememberCoroutineScope()
        val auth = remember { Firebase.auth }
        var firebaseUser: FirebaseUser? by remember { mutableStateOf(null) }
        var userEmail by remember { mutableStateOf("") }
        var userPassword by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }

        if (firebaseUser == null) {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        placeholder = { Text("Email address") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            hideKeyboard()
                        }),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.onSurface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = userPassword,
                        onValueChange = { userPassword = it },
                        placeholder = { Text("Password") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            hideKeyboard()
                        }),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(image, contentDescription = "Toggle password visibility")
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.onSurface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        hideKeyboard()
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
}