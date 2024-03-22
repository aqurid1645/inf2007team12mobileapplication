package com.inf2007team12mobileapplication.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoanPage() {
    var studentName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loanPeriod by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Loan Page", color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        InfoItem(label = "Item scanned:", value = "Raspberry Pi Pico")
        InfoItem(label = "Model Number:", value = "A1B3C3")
        EditItem(label = "Student Name:", value = studentName, onValueChange = { studentName = it })
        EditItem(label = "Student ID:", value = studentId, onValueChange = { studentId = it })
        EditItem(label = "Password:", value = password, onValueChange = { password = it }, visualTransformation = PasswordVisualTransformation())
        EditItem(label = "Loan Period:", value = loanPeriod, onValueChange = { loanPeriod = it })

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                // Handle the form submission
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value)
    }
}

@Composable
fun EditItem(label: String, value: String, onValueChange: (String) -> Unit, visualTransformation: VisualTransformation = VisualTransformation.None) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label)
        TextField(
            value = value,
            onValueChange = onValueChange,
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoanPagePreview() {
    LoanPage()
}