package com.inf2007team12mobileapplication.presentation.lecturerrecord

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LecturerRecordScreen() {
    var studentNameOrId by remember { mutableStateOf("") }
    var resourceType by remember { mutableStateOf("") }
    var dates by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Student List")
        Spacer(modifier = Modifier.height(16.dp))
        SearchField(hint = "Student Name/ID", text = studentNameOrId, onTextChange = { studentNameOrId = it })
        SearchField(hint = "Resource Type", text = resourceType, onTextChange = { resourceType = it })
        SearchField(hint = "Dates", text = dates, onTextChange = { dates = it })

        Spacer(modifier = Modifier.height(16.dp))
        ItemList() // This composable will display the list of items
    }
}

@Composable
fun SearchField(hint: String, text: String, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(hint) },
        trailingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@Composable
fun ItemList() {
    // Replace this list with your actual data
    val items = listOf(
        Pair("Motor", "123456"),
        Pair("IR sensor", "123456"),
        Pair("Pixel 7", "123456"),
        Pair("Raspberry Pi", "123456"),
        Pair("Raspberry Pi Pico", "123456")
    )

    items.forEach { (item, modelNo) ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(item)
            Text(modelNo)
            Text("0/0/0000 - infinity")
        }
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun LecturerRecordScreenPreview() {
    LecturerRecordScreen()
}