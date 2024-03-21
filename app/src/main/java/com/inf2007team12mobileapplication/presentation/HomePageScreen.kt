package com.example.project

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePageScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resources Management", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO: handle user profile click */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "User Profile")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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

            // Buttons for different actions
            ActionButton(Icons.Default.CameraAlt, "Scan resource")
            ActionButton(Icons.Default.ReportProblem, "Report a defect")
            ActionButton(Icons.Default.AccessTime, "Extend a loan")
            ActionButton(Icons.Default.Search, "Search for a resource")
            ActionButton(Icons.Default.List, "Items borrowed")
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(onClick = { /* TODO: handle button click */ }) {
            Icon(icon, contentDescription = text)
        }
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun HomePageScreenPreview() {
    HomePageScreen()
}