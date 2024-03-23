package com.inf2007team12mobileapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inf2007team12mobileapplication.presentation.biometric.EnableBiometricScreen
import com.inf2007team12mobileapplication.presentation.login.SignInScreen
import com.inf2007team12mobileapplication.presentation.profile.ProfileScreen
import com.inf2007team12mobileapplication.presentation.signup.SignUpScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    var activity:MainActivity = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                modifier = Modifier,
                navController = navController,
                startDestination = "signin"
            ) {

                composable("signup") {
                    SignUpScreen(navController=navController)
                }
                composable("signin") {
                    SignInScreen(navController=navController, activity=activity)
                }
                composable("enablebiometric") {
                    EnableBiometricScreen(navController=navController, activity=activity)
                }
                composable("profile") {
                    ProfileScreen(navController=navController)
                }
            }
        }
    }
}