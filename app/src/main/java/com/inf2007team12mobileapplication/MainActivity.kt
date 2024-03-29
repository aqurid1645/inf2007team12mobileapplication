package com.inf2007team12mobileapplication


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.inf2007team12mobileapplication.presentation.camera.CameraScreen
import com.inf2007team12mobileapplication.presentation.homepage.HomePageScreen
import com.inf2007team12mobileapplication.presentation.search.InventoryScreen
import com.inf2007team12mobileapplication.presentation.login.SignInScreen
import com.inf2007team12mobileapplication.presentation.profile.ChangePasswordScreen
import com.inf2007team12mobileapplication.presentation.profile.ProfileScreen
import com.inf2007team12mobileapplication.presentation.report.ReportScreen
import com.inf2007team12mobileapplication.presentation.signup.SignUpScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                MainScreen()
            }
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val screensWithIcons = listOf(Screen.Home, Screen.Camera, Screen.Report)
    val hideBottomBarRoutes = listOf(Screen.SignIn.route, Screen.SignUp.route) // Define routes where the bottom bar should be hidden
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes) {
                BottomNavigationBar(navController, screensWithIcons)
            }
        }
    ) {
        NavHost(navController = navController, startDestination = Screen.SignIn.route) {
            composable(Screen.SignUp.route) { SignUpScreen(navController) }
            composable(Screen.SignIn.route) { SignInScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable(Screen.Camera.route) { CameraScreen(navController) }
            composable(Screen.ResetPassword.route) { ChangePasswordScreen(navController) }
            composable(Screen.Report.route) { ReportScreen(navController) }
            composable(Screen.Home.route) { HomePageScreen(navController) }
            composable(Screen.Inventory.route) { InventoryScreen(navController) }

            // Define other composable screens here
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, screens: List<Screen>) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val currentRoute = navController.currentDestination?.route
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = { screen.icon?.let { Icon(it, contentDescription = null) } ?: Spacer(Modifier) },
                label = { Text(screen.route) },
                selected = currentRoute == screen.route,
                onClick = {
                    // Clear the back stack when navigating to avoid going back to the SignIn screen
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
private val DarkColorPalette = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC5),
    // Define other colors for your dark theme
)

private val LightColorPalette = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC5),
    // Define other colors for your light theme
    surface = Color.White, // Color for the BottomNavigation background
    onSurface = Color.Black // Color for the BottomNavigation text and icons
    // Define other colors for your light theme
)

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
