package com.inf2007team12mobileapplication.presentation.extension

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.Loan

@Composable
fun ExtensionScreen(navController: NavController, viewModel: ExtensionScreenViewModel = hiltViewModel()) {
    val loansResource by viewModel.loans.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUserLoans()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Loans", style = MaterialTheme.typography.headlineMedium)

        // Make the loan entries scrollable
        when (loansResource) {
            is Resource.Success -> {
                val loans = loansResource.data.orEmpty()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(loans) { loan ->
                        LoanItem(loan = loan, onExtendClick = {
                            loan.endDate?.let { endDate ->
                                viewModel.extendLoanEndDate(loan.loanId, endDate)
                            }
                        })
                    }
                }
            }
            is Resource.Loading -> {
                // Show loading state
                // ...
            }
            is Resource.Error -> {
                // Show error state
                // ...
            }

            is Resource.Error -> TODO()
            is Resource.Loading -> TODO()
            is Resource.Success -> TODO()
        }
    }
}

@Composable
fun LoanItem(loan: Loan, onExtendClick: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Loan ID: ${loan.loanId}", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { isExpanded = !isExpanded }) {
                    Text(if (isExpanded) "Collapse" else "Expand")
                }
                Button(onClick = onExtendClick) {
                    Text("Extend")
                }
            }

            // Details are displayed directly within the column, without internal scrolling
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Text("Product ID: ${loan.productId}")
                    Text("Product Category: ${loan.productCategory}")
                    Text("Product Description: ${loan.productDescription}")
                    Text("Product Barcode ID: ${loan.productBarcodeID}")
                    Text("Product Name: ${loan.productName}")
                    loan.startDate?.let {
                        Text("Start Date: ${it.toDate().toString()}")
                    }
                    loan.endDate?.let {
                        Text("End Date: ${it.toDate().toString()}")
                    }
                    Text("Status: ${loan.status}")
                }
            }
        }
    }
}