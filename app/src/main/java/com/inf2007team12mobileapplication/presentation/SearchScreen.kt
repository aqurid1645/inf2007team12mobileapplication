package com.inf2007team12mobileapplication.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Resources Management")
        Spacer(modifier = Modifier.height(8.dp))
        SearchField(hint = "Resource Type")
        SearchField(hint = "Loan Start Date")
        SearchField(hint = "Loan Start Date") // This should probably have a different hint or identifier

        Spacer(modifier = Modifier.height(16.dp))
        ResourceList()
    }
}

@Composable
private fun SearchField(hint: String) {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(hint) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ResourceList() {
    Column {
        // This could be a list of items fetched from a database or server
        val items = listOf("Raspberry Pi") // Replace with your actual data source

        items.forEach { itemName ->
            ResourceListItem(itemName = itemName, quantityAvailable = 100, availableLoanDate = "0/0/0000 - infinity")
        }
    }
}

@Composable
fun ResourceListItem(itemName: String, quantityAvailable: Int, availableLoanDate: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(itemName)
        Text(quantityAvailable.toString())
        Text(availableLoanDate)
    }
    Divider()
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}