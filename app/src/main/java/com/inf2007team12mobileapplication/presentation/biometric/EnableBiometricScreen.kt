package com.inf2007team12mobileapplication.presentation.biometric

import android.content.Context
import android.net.wifi.hotspot2.pps.Credential.UserCredential
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.inf2007team12mobileapplication.BiometricPromptUtils
import com.inf2007team12mobileapplication.CIPHERTEXT_WRAPPER
import com.inf2007team12mobileapplication.CryptographyManager
import com.inf2007team12mobileapplication.SECRET_KEY_NAME
import com.inf2007team12mobileapplication.SHARED_PREFS_FILENAME
import com.inf2007team12mobileapplication.presentation.login.SignInViewModel
import kotlinx.coroutines.launch


private lateinit var cryptographyManager: CryptographyManager
private lateinit var userCredential: UserCredential
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnableBiometricScreen(
    navController: NavController,
    activity: AppCompatActivity,
    viewModel: SignInViewModel = hiltViewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = viewModel.signInState.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, end = 30.dp),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) { Text(
        modifier = Modifier.padding(bottom = 10.dp),
        text = "Enable Biometric Login",
        fontWeight = FontWeight.Bold,
        fontSize = 35.sp,

        )
        Text(
            text = "Enter your login ID and password to confirm activation of Biometric Login",
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = Color.Gray,
            fontFamily = FontFamily.Default
        )
        TextField(
            value = email,
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.LightGray,
                cursorColor = Color.Black,
                disabledLabelColor = Color.LightGray, unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ), shape = RoundedCornerShape(8.dp), singleLine = true, placeholder = {
                Text(text = "Email")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.LightGray,
                cursorColor = Color.Black,
                disabledLabelColor = Color.LightGray, unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ), shape = RoundedCornerShape(8.dp), singleLine = true, placeholder = {
                Text(text = "Password")
            }
        )

        Row(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Button(
                onClick = {
                    navController.navigate("signin")
                },
                modifier = Modifier
                    .padding(top = 20.dp, start = 15.dp, end = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black, contentColor = Color.White
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(text = "CANCEL", color = Color.White, modifier = Modifier.padding(7.dp))
            }

            Button(
                onClick = {
                    scope.launch {
                        viewModel.loginUser(email, password)
                    }
                },
                modifier = Modifier
                    .padding(top = 20.dp, start = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black, contentColor = Color.White
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(text = "AUTHORIZE", color = Color.White, modifier = Modifier.padding(7.dp))
            }
        }
    }

    LaunchedEffect(key1 = state.value?.isSuccess) {
        scope.launch {
            if (state.value?.isSuccess?.isNotEmpty() == true) {
                val success = state.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                userCredential = UserCredential()
                userCredential.username = email
                userCredential.password = password
                showBiometricPromptForEncryption(activity)
                navController.navigate("signin")
            }
        }
    }
    LaunchedEffect(key1 = state.value?.isError) {
        scope.launch {
            if (state.value?.isError?.isNotBlank() == true) {
                val error = state.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}

private fun showBiometricPromptForEncryption(activity: AppCompatActivity) {
    val canAuthenticate = BiometricManager.from(activity.applicationContext).canAuthenticate()
    if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
        cryptographyManager = CryptographyManager()
        val cipher = cryptographyManager.getInitializedCipherForEncryption(SECRET_KEY_NAME)
        val biometricPrompt =
            BiometricPromptUtils.createBiometricPrompt(activity, ::encryptAndStoreUserCredentials)
        val promptInfo = BiometricPromptUtils.createPromptInfo(activity)
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }
}


private fun encryptAndStoreUserCredentials(activity: AppCompatActivity, authResult: BiometricPrompt.AuthenticationResult) {
    authResult.cryptoObject?.cipher?.apply {
        userCredential?.let { userCredential ->
            val gson = Gson()
            val encryptedServerTokenWrapper = cryptographyManager.encryptData(gson.toJson(userCredential), this)
            cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                encryptedServerTokenWrapper,
                activity.applicationContext,
                SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE,
                CIPHERTEXT_WRAPPER
            )
        }
    }
    activity.finish()
}