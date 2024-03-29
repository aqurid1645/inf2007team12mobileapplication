package com.inf2007team12mobileapplication.presentation.search

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun InventoryScreen(navController: NavController, viewModel: InventoryScreenViewModel = hiltViewModel()) {
    var searchText by remember { mutableStateOf("") }
    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resources Management") },
                backgroundColor = Color.White,
                contentColor = Color.Black
            )
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search Icon")
                },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            DropdownField("Resource Type")
            DropdownField("Loan Start Date")
            DropdownField("Loan End Date")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.getProduct(searchText)
                    // Log the search term or perform a search operation
                    println("Search submitted for: $searchText")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search")
            }

            // Display product details if available
            state.productDetails?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it)
            }

            // Display list of items
            // You need to use LazyColumn for actual list implementation
            Text("List of items will be displayed here.")
        }
    }
}

@Composable
fun DropdownField(label: String) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxWidth()) {
        ClickableText(
            text = AnnotatedString(if (selectedOption.isEmpty()) label else selectedOption),
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                selectedOption = "Option 1"
                expanded = false
            }) {
                Text("Option 1")
            }
            DropdownMenuItem(onClick = {
                selectedOption = "Option 2"
                expanded = false
            }) {
                Text("Option 2")
            }
            // Add more DropdownMenuItems as needed
        }
    }
}
