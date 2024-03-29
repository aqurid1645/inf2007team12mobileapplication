package com.inf2007team12mobileapplication.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun InventoryScreen(navController: NavController,viewModel: InventoryScreenViewModel = hiltViewModel()) {
    var text by remember { mutableStateOf(TextFieldValue()) }
    val state = viewModel.state.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter text here") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.getProduct(text.text)
                // Handle button click here, for example, print the text
                println("Text submitted: ${text.text}")
            }
        ) {
            Text("Submit")
        }
        state.productDetails?.let { Text(text = it) }
    }
}