package com.inf2007team12mobileapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.firebase.FirebaseApp
import com.inf2007team12mobileapplication.presentation.camera.CameraScreen
import com.inf2007team12mobileapplication.presentation.homepage.HomePageScreen
import com.inf2007team12mobileapplication.presentation.login.SignInScreen
import com.inf2007team12mobileapplication.presentation.notification.NotificationList
import com.inf2007team12mobileapplication.presentation.notification.NotificationScreen
import com.inf2007team12mobileapplication.presentation.camera.CameraScreen
import com.inf2007team12mobileapplication.presentation.catalog.ProductCatalogScreen
import com.inf2007team12mobileapplication.presentation.homepage.HomePageScreen
import com.inf2007team12mobileapplication.presentation.login.SignInScreen
import com.inf2007team12mobileapplication.presentation.profile.ChangePasswordScreen
import com.inf2007team12mobileapplication.presentation.profile.ProfileScreen
import com.inf2007team12mobileapplication.presentation.report.ReportScreen
import com.inf2007team12mobileapplication.presentation.search.InventoryScreen
import com.inf2007team12mobileapplication.presentation.signup.SignUpScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            FirebaseApp.initializeApp(applicationContext)
        } else {
            Toast.makeText(
                applicationContext,
                "Notification will not receive !!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    var editor: SharedPreferences.Editor? = null
    var token: String? = null
    var notification: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            FirebaseApp.initializeApp(applicationContext)
        } else {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({ //Do something after 500ms
                askNotificationPermission()
            }, 500)
        }
        try {
            notification = getIntent().extras!!.getString("id")
        }catch (e:NullPointerException){
        }catch (e:Exception){
        }
        setContent {
            MyAppTheme {
                if (notification != null) {
                    NotificationScreen()
                }else {
                    MainScreen()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val notification = intent.extras!!.getString("notification")
        setContent {
            MyAppTheme {
                if (notification != null) {
                    NotificationScreen()
                }
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                FirebaseApp.initializeApp(applicationContext)
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val screensWithIcons = listOf(Screen.Home, Screen.Camera, Screen.Report,Screen.ProductCatalog)
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
            composable(Screen.ProductCatalog.route) { ProductCatalogScreen(navController)}

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
