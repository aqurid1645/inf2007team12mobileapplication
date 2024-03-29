package com.inf2007team12mobileapplication.presentation.camera

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


@Composable
fun CameraScreen(navController: NavController, viewModel: CameraScreenViewModel = hiltViewModel()) {
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Welcome ${viewModel.getuseremail() ?: "Guest"}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.startScanning() }) {
                Text(text = "Borrow")
            }

            // Check if there's a product ID available when showing the confirmation dialog
            state.scannedProductId?.let { scannedProductId ->
                if (state.showConfirmationDialog) {
                    BorrowConfirmationDialog(
                        productId = scannedProductId,
                        onConfirm = { viewModel.confirmBorrowing() },
                        onDismiss = { viewModel.resetDialog() }
                    )
                }
            }
        }
    }

    // Show error as Toast
    if (state.showError && state.errorMessage.isNotEmpty()) {
        Toast.makeText(context, state.errorMessage, Toast.LENGTH_LONG).show()
        viewModel.clearError() // Clear the error after showing it
    }

    if (state.showLoanDetailsDialog && state.loanDetails != null) {
        AlertDialog(
            onDismissRequest = { viewModel.resetDialog() },
            title = { Text(text = "Loan Details") },
            text = { Text(text = state.loanDetails) },
            confirmButton = {
                Button(onClick = { viewModel.resetDialog() }) {
                    Text("OK")
                }
            }
        )
    }
}



@Composable
fun BorrowConfirmationDialog(productId: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Borrow Product") },
        // Include the product ID in the dialog text for clarity
        text = { Text("Do you want to borrow the product : $productId?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
fun LoanDetailsDialog(loanDetails: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Loan Details") },
        text = { Text(loanDetails) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
