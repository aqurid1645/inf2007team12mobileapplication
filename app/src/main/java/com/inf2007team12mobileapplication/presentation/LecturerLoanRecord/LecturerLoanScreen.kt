package com.inf2007team12mobileapplication.presentation.lecturerrecord

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.Loan
import com.inf2007team12mobileapplication.presentation.LecturerLoanRecord.LecturerLoanViewModel
import kotlinx.coroutines.flow.StateFlow
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.*
import java.util.Date
import java.util.Locale

// Assuming LecturerViewModel is correctly injected or provided.
@Composable
fun LecturerLoanScreen(navController: NavController, viewModel: LecturerLoanViewModel = hiltViewModel()) {
    var showFilterDialog by remember { mutableStateOf(false) }
    var studentId by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Search and Filter Student Loans", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.size(16.dp))
        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Student ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(onClick = { viewModel.fetchLoansByStudentId(studentId) }) { Text("Search") }
        Spacer(modifier = Modifier.size(8.dp))
        Button(onClick = { showFilterDialog = true }) { Text("Filters") }
        LoanList(loansResourceFlow = viewModel.loans)
    }

    if (showFilterDialog) {
        FilterDialog(
            viewModel = viewModel,
            onDismissRequest = { showFilterDialog = false }
        )
    }
}


@Composable
fun FilterDialog(viewModel: LecturerLoanViewModel, onDismissRequest: () -> Unit) {
    var productName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Apply Filters") },
        text = {
            Column {
                TextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product Name") }
                )
                DateField("Start Date", startDate) { newDate -> startDate = newDate }
                DateField("End Date", endDate) { newDate -> endDate = newDate }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Adjust applyFilters call to ensure date strings are properly formatted or handled if blank
                val formattedStartDate = if (startDate.isBlank()) null else startDate
                val formattedEndDate = if (endDate.isBlank()) null else endDate
                viewModel.applyFilters(formattedStartDate, formattedEndDate, productName, null, null)
                onDismissRequest()
            }) { Text("Apply") }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) { Text("Cancel") }
        }
    )
}


@Composable
fun DateField(label: String, date: String, onDateChange: (String) -> Unit) {
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Convert the provided date string back to a Date object, or use the current date if the string is blank
    val selectedDate = try {
        if (date.isNotBlank()) dateFormatter.parse(date) else Date()
    } catch (e: ParseException) {
        Date() // Fallback to the current date in case of parsing failure
    }

    // Extract the year, month, and day from the selected date
    val calendar = getInstance().apply {
        time = selectedDate
    }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    Button(onClick = {
        val picker = android.app.DatePickerDialog(context, { _, year, month, day ->
            val cal = getInstance().apply {
                set(year, month, day)
            }
            onDateChange(dateFormatter.format(cal.time))
        }, calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH))

        picker.show()
    }) {
        Text(text = if (date.isBlank()) label else date)
    }
}







@Composable
fun LoanList(loansResourceFlow: StateFlow<Resource<List<Loan>>>) {
    val loansResource by loansResourceFlow.collectAsState()

    when (loansResource) {
        is Resource.Loading -> {
           Text("Start Searching")
        }
        is Resource.Success -> {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                // No need for casting; directly use the data with smart casting
                loansResource.data?.let { loans ->
                    items(loans) { loan ->
                        LoanItem(loan = loan)
                    }
                }
            }
        }
        is Resource.Error -> Text(
            text = "Error: ${loansResource.message}",
            modifier = Modifier.padding(16.dp)
        )
    }
}




@Composable
fun LoanItem(loan: Loan) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Loan ID: ${loan.loanId}", style = MaterialTheme.typography.bodyLarge)
        Text("Product Name: ${loan.productName}", style = MaterialTheme.typography.bodyMedium)
        Text("Product Category: ${loan.productCategory}", style = MaterialTheme.typography.bodyMedium)
        Text("Product Description: ${loan.productDescription}", style = MaterialTheme.typography.bodyMedium)
        Text("Product ID: ${loan.productId}", style = MaterialTheme.typography.bodyMedium)
        Text("Product Barcode ID: ${loan.productBarcodeID}", style = MaterialTheme.typography.bodyMedium)
        Text("Status: ${loan.status}", style = MaterialTheme.typography.bodyMedium)
        // Formatting dates for display
        val dateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.getDefault())
        loan.startDate?.toDate()?.let {
            Text("Start Date: ${dateFormat.format(it)}", style = MaterialTheme.typography.bodyMedium)
        }
        loan.endDate?.toDate()?.let {
            Text("End Date: ${dateFormat.format(it)}", style = MaterialTheme.typography.bodyMedium)
        }
        Divider(modifier = Modifier.padding(vertical = 4.dp))
    }
}
