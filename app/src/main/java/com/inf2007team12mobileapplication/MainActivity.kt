package com.inf2007team12mobileapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inf2007team12mobileapplication.presentation.signup.SignUpScreen
import com.inf2007team12mobileapplication.presentation.login.SignInScreen
import com.inf2007team12mobileapplication.presentation.profile.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                    SignInScreen(navController=navController)
                }
                composable("profile") {
                    ProfileScreen(navController=navController)
                }
            }
        }
    }
}