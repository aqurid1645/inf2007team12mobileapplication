package com.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ExtensionScreen() {
    var selectedItem by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newEndDate by remember { mutableStateOf("") }

    // Mock data for the dropdown menu, replace with your data
    val items = listOf("Item 1", "Item 2", "Item 3")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Extension of loan")
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown menu to select item to extend loan


        // Date picker or another dropdown for new end date
        // For now, it's just a TextField
        OutlinedTextField(
            value = newEndDate,
            onValueChange = { newEndDate = it },
            label = { Text("New end date") },
            trailingIcon = {},
            readOnly = true, // The actual implementation should open a date picker dialog
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Handle the 'Done' action */ }),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                // Handle the submit action
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Submit")
        }
    }
}

// Re-use the CustomDropdownMenu from the previous example here

@Preview(showBackground = true)
@Composable
fun ExtensionScreenPreview() {
    ExtensionScreen()
}