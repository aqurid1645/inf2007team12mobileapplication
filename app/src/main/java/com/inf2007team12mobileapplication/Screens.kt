package com.inf2007team12mobileapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector? = null) {
    object SignUp : Screen("signup")
    object SignIn : Screen("signin")
    object NotificationScreen : Screen("notification")

    object Profile : Screen("profile")
    // Object CreateProfile : Screen("createprofile")
    object Camera : Screen("Camera", Icons.Default.CameraAlt)
    object ResetPassword : Screen("resetpassword")
    object Report : Screen("Report", Icons.Default.Report)
    object Home : Screen("Home", Icons.Default.Home)
    object Inventory : Screen("inventory", Icons.Default.Search)

    object LecturerLoan : Screen("LecturerLoan", Icons.Default.Search)
    object Extension : Screen("extension")

    object ProductCatalog: Screen("Catalog",Icons.AutoMirrored.Filled.ViewList)


}


