package com.inf2007team12mobileapplication.presentation.notification

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController, viewModel: NotificationViewModel = hiltViewModel()) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Notifications", fontWeight = FontWeight.Bold) }) }
    ) { paddingValues ->
        val notifications by viewModel.notifications.collectAsState()
        LazyColumn(contentPadding = paddingValues) {
            items(notifications, key = { it.notificationId }) { notification ->
                NotificationItem(
                    notificationText = notification.message,
                    onApproveClicked = {
                        viewModel.sendApprovalNotification(
                            studentUserId = notification.userId,
                            reportId = notification.relatedId,
                            productName = notification.productName
                        )
                    }
                )
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun NotificationItem(notificationText: String, onApproveClicked: () -> Unit) {
    var approvalSent by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(30.dp)) {
        Text(notificationText)

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            if (!approvalSent) {
                Button(onClick = {
                    onApproveClicked()
                    approvalSent = true
                }, modifier = Modifier.weight(1f)) {
                    Text("Approve", color = Color.White)
                }
            } else {
                Text("Approval has been sent to student", modifier = Modifier.weight(1f), color = Color.Green)
            }
        }
    }
}


@Composable
fun NotificationList(viewModel: NotificationViewModel, paddingValues: PaddingValues) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        notifications.forEach { notification ->
            NotificationItem(
                notificationText = notification.message,
                onApproveClicked = {
                    viewModel.sendApprovalNotification(
                        studentUserId = notification.userId,
                        reportId = notification.relatedId,
                        productName = notification.productName
                    )
                }
            )
            Divider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}

