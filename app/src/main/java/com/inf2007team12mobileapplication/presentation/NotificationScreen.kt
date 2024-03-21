package com.example.project

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification", fontWeight = FontWeight.Bold) }
            )
        }
    ) {
        NotificationList()
    }
}

@Composable
fun NotificationList() {
    // Replace this with your actual data source
    val notifications = listOf(
        "Student A requested an exchange due to defective equipment 456",
        "Student B requested an extension of equipment 123",
        "Student C requested an exchange due to defective equipment 978"
    )

    Column {
        notifications.forEach { notification ->
            NotificationItem(notificationText = notification)
            Divider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}

@Composable
fun NotificationItem(notificationText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(notificationText)

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Button(
                onClick = { /* Handle approve action */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Approve", color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { /* Handle reject action */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reject", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    NotificationScreen()
}