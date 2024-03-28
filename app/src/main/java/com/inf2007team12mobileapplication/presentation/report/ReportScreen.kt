package com.inf2007team12mobileapplication.presentation.report

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.Loan
import com.inf2007team12mobileapplication.data.model.Report
import java.util.UUID

@Composable
fun ReportScreen(navController: NavController, viewModel: ReportScreenViewModel = hiltViewModel()) {
    val scaffoldState = rememberScaffoldState()
    val submissionStatus by viewModel.submissionStatus.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(key1 = true) {
        viewModel.fetchUserLoans()
    }

    val loansResource by viewModel.loans.collectAsState()
    var selectedLoan by remember { mutableStateOf<Loan?>(null) }
    var problemType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Report a Problem")
            Spacer(modifier = Modifier.height(8.dp))

            ProblemTypeDropdownMenu(problemType) { selectedType ->
                problemType = selectedType
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (loansResource) {
                is Resource.Loading -> Text("Loading loans...")
                is Resource.Success -> {
                    LoanDropdownMenu(loansResource.data ?: listOf(), selectedLoan) { loan ->
                        selectedLoan = loan
                    }
                }
                is Resource.Error -> Text("Error loading loans: ${loansResource.message}")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(0.95f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (selectedLoan != null && problemType.isNotEmpty() && description.isNotEmpty()) {
                        keyboardController?.hide()
                        val report = Report(
                            reportId = UUID.randomUUID().toString(),
                            userId = viewModel.getCurrentUserId(),
                            loanId = selectedLoan!!.loanId,
                            productId = selectedLoan!!.productId,
                            description = description,
                            problemType = problemType,
                            productName = selectedLoan!!.productName,
                            productDescription = selectedLoan!!.productDescription,
                            productCategory = selectedLoan!!.productCategory,
                            productBarcodeID = selectedLoan!!.productBarcodeID
                        )
                        viewModel.reportDefectiveProduct(report)
                    }
                },
                enabled = selectedLoan != null && problemType.isNotEmpty() && description.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Report")
            }
        }
    }

    // Handle the display of submission status
    submissionStatus?.let { status ->
        when (status) {
            is Resource.Success -> {
                LaunchedEffect(status) {
                    scaffoldState.snackbarHostState.showSnackbar("Report submitted successfully")
                    viewModel.clearSubmissionStatus()
                }
            }
            is Resource.Error -> {
                LaunchedEffect(status) {
                    scaffoldState.snackbarHostState.showSnackbar("Error submitting report: ${status.message}")
                    viewModel.clearSubmissionStatus()
                }
            }
            else -> {
                LaunchedEffect(status) {
                    scaffoldState.snackbarHostState.showSnackbar("Please try again: $${status.message}")
                    viewModel.clearSubmissionStatus()
                }
            }
        }
    }
}

// Other composables remain the same

@Composable
fun ProblemTypeDropdownMenu(
    selectedProblemType: String,
    onProblemTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val problemTypes = listOf("Defects", "Hardware Issue", "Software Issue", "Others")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (selectedProblemType.isNotEmpty()) selectedProblemType else "Select Problem Type",
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown Arrow"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()

        ) {
            problemTypes.forEach { type ->
                DropdownMenuItem(
                    onClick = {
                        onProblemTypeSelected(type)
                        expanded = false
                    },
                    text = { Text(text = type, style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun LoanDropdownMenu(
    loans: List<Loan>,
    selectedLoan: Loan?,
    onLoanSelected: (Loan) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = selectedLoan?.productName ?: "Select a Loan"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }

            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown Arrow"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()

        ) {
            loans.forEach { loan ->
                DropdownMenuItem(
                    onClick = {
                        onLoanSelected(loan)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = "Product name:${loan.productName}, Product Barcode ID:(${loan.productBarcodeID})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}