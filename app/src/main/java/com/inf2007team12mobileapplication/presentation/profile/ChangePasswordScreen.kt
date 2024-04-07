package com.inf2007team12mobileapplication.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (loading) {
        CircularProgressIndicator()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            LaunchedEffect(errorMessage) {
                // Clear the error message after displaying it, so it disappears from the UI.
                delay(3000)  // Keep the message for 3 seconds.
                errorMessage = ""
            }
        }

        PasswordInputField(value = oldPassword, label = "Old Password") { oldPassword = it }
        PasswordInputField(value = newPassword, label = "New Password") { newPassword = it }
        PasswordInputField(value = confirmPassword, label = "Confirm New Password") { confirmPassword = it }

        Button(
            onClick = {
                if (newPassword != confirmPassword) {
                    errorMessage = "New Password and Confirm Password do not match."
                    oldPassword = ""  // Clear the fields
                    newPassword = ""
                    confirmPassword = ""
                    return@Button
                }

                loading = true
                val user = auth.currentUser
                user?.let {
                    val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
                    it.reauthenticate(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                                loading = false
                                if (updateTask.isSuccessful) {
                                    scope.launch {
                                        Toast.makeText(context, "Password updated successfully. Logging out...", Toast.LENGTH_SHORT).show()

                                        delay(2000) // Wait for 2 seconds before logging out.
                                        auth.signOut()  // Log out the user
                                        navController.navigate("signin") {
                                        }


                                    }
                                } else {
                                    errorMessage = "Error updating password."
                                }
                            }
                        } else {
                            loading = false
                            errorMessage = "Error re-authenticating. Check your old password."
                            oldPassword = ""  // Clear the fields for retry
                            newPassword = ""
                            confirmPassword = ""
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = oldPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank() && !loading
        ) {
            Text("Change Password")
        }
    }
}


@Composable
fun PasswordInputField(value: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        modifier = Modifier.fillMaxWidth()
    )
}
