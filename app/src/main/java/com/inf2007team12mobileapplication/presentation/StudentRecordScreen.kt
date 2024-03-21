package com.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
fun StudentRecordScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Records of items borrowed")
        Spacer(modifier = Modifier.height(16.dp))
        SearchField(hint = "Resource Type")
        SearchField(hint = "Dates")
        Spacer(modifier = Modifier.height(16.dp))
        BorrowedItemsList()
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
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun BorrowedItemsList() {
    // In a real application, the list of items would come from a database or some state
    val borrowedItems = listOf(
        "Raspberry Pi",
        "Raspberry Pi Pico",
        "Motor",
        "IR sensor",
        "Pixel 7" // Replace these with actual data
    )

    borrowedItems.forEach { itemName ->
        BorrowedItemRow(itemName, "123456", "0/0/0000 - infinity")
    }
}

@Composable
fun BorrowedItemRow(item: String, modelNumber: String, loanDate: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(item)
        Text(modelNumber)
        Text(loanDate)
    }
    Divider()
}

@Preview(showBackground = true)
@Composable
fun StudentRecordScreenPreview() {
    StudentRecordScreen()
}