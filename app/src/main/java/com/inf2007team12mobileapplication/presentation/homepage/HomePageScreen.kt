package com.inf2007team12mobileapplication.presentation.homepage

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.inf2007team12mobileapplication.R
import com.inf2007team12mobileapplication.data.Resource

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePageScreen(
    navController: NavController,
    viewModel: HomePageViewModel = hiltViewModel()
) {

    // Observe the token update status
    viewModel.tokenUpdateStatus.collectAsState().value
    val userRoleStatus = viewModel.userRole.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resources Management", fontWeight = FontWeight.Bold) },
                actions = {
                    if (userRoleStatus is Resource.Success && ( userRoleStatus.data == "lecturer")) {
                        IconButton(onClick = { navController.navigate("notification") }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", Modifier.size(30.dp))
                        }
                    }
                    IconButton(onClick = {

                        navController.navigate("profile")
                    }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "User Profile", Modifier.size(30.dp))
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Replace with the actual image resource name for your Raspberry Pi image
            val image = painterResource(id = R.drawable.ic_raspberry_pi)
            Image(
                painter = image,
                contentDescription = "Raspberry Pi",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))


            ActionButton(icon = Icons.Default.CameraAlt, text = "Scan resource") {
                    navController.navigate("Camera") // Replace with your correct route
                }
            ActionButton(icon = Icons.Default.ReportProblem, text = "Report a defect") {
                navController.navigate("Report") // Replace with your correct route
            }
            if (userRoleStatus is Resource.Success && userRoleStatus.data == "student") {
                ActionButton(icon = Icons.Default.AccessTime, text = "Extend a loan") {
                    navController.navigate("extension")
                }
            }
            ActionButton(icon = Icons.Default.Search, text = "Search for a resource") {
                navController.navigate("inventory/ ")
            }

            if (userRoleStatus is Resource.Success && userRoleStatus.data == "lecturer") {
                ActionButton(icon = Icons.Default.Person, text = "Lecturer Loan") {
                    navController.navigate("LecturerLoan")
                }
            }
            ActionButton(icon = Icons.AutoMirrored.Filled.ViewList, text = "Product Catalog") {
                navController.navigate("Catalog")
            }
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick) // Handle button click
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(40.dp)) // Adjust icon size if desired
        Text(text)
    }
}