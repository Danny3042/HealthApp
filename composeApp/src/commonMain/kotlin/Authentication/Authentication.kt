package Authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

const val SignUpScreen = "SignUp"
const val LoginScreen = "Login"

class Authentication {

    @Composable
    fun Login(navController: NavController) {
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
                    Text("Sign in")
                }
                OutlinedButton(onClick = {
                    navController.navigate(SignUpScreen)
                }) {
                    Text("Sign Up")
                }
            }
        }
        //TODO: navigate to hero screen
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
    }
    // TODO: navigate to hero screen
}