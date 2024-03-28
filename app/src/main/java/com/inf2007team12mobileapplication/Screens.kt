package com.inf2007team12mobileapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Report
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector? = null) {
    object SignUp : Screen("signup")
    object SignIn : Screen("signin")
    object Profile : Screen("profile")
    // Object CreateProfile : Screen("createprofile")
    object Camera : Screen("camera", Icons.Default.CameraAlt)
    object ResetPassword : Screen("resetpassword")
    object Report : Screen("report", Icons.Default.Report)
    object Home : Screen("home", Icons.Default.Home)

}
