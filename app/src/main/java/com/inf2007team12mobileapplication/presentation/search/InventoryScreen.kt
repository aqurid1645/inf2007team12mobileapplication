package com.inf2007team12mobileapplication.presentation.search

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun InventoryScreen(productName: String, navController: NavHostController, viewModel: InventoryScreenViewModel = hiltViewModel()
) {

    var searchText by remember { mutableStateOf("") }
    val state = viewModel.state.collectAsState().value

    if (productName.isNotBlank()) {
    LaunchedEffect(productName) {
            viewModel.getProduct(productName)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory Search") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            Surface(
                color = MaterialTheme.colors.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Search Icon")
                        },
                        label = { Text("Search") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            viewModel.getProduct(searchText)
                        }
                    ) {
                        Text("Search")
                    }
                }
            }

            // Filters
            Surface(
                color = MaterialTheme.colors.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(16.dp)
                ) {
                    DropdownField("Resource Type")
                    DropdownField("Loan Start Date")
                    DropdownField("Loan End Date")
                }
            }

            // Search Results
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                state.productDetails?.let { productDetails ->
                    item {
                        Text(
                            text = "Search Results",
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    productDetails.split("\n").forEach { line ->
                        val parts = line.split(": ")
                        if (parts.size == 2) {
                            val (key, value) = parts
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$key: ",
                                        style = MaterialTheme.typography.subtitle1,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = value,
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        } else {
                            item {
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownField(s: String) {

}
