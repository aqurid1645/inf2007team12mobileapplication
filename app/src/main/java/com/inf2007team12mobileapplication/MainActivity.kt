package com.inf2007team12mobileapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inf2007team12mobileapplication.presentation.camera.CameraScreen
import com.inf2007team12mobileapplication.presentation.extension.ExtensionScreen
import com.inf2007team12mobileapplication.presentation.login.SignInScreen
import com.inf2007team12mobileapplication.presentation.profile.ChangePasswordScreen
import com.inf2007team12mobileapplication.presentation.profile.ProfileScreen
import com.inf2007team12mobileapplication.presentation.signup.SignUpScreen
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
                    ProfileScreen(navController = navController)
                }
              /*  composable("createprofile") {
                    CreateProfileScreen(navController = navController)
                }*/
                composable("camera") {
                    CameraScreen(navController = navController)
                }
                composable("resetpassword") {
                    ChangePasswordScreen(navController = navController)
                }
                composable("extension") {
                    ExtensionScreen(navController = navController)
                }

               /* composable("report") {
                   ReportScreen(navController = navController)
                }
*/
            }
        }
    }
}