package com.inf2007team12mobileapplication.presentation.camera

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.inf2007team12mobileapplication.presentation.homepage.ActionButton


@Composable
fun CameraScreen(navController: NavController, viewModel: CameraScreenViewModel = hiltViewModel()) {
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))


          FloatingActionButton(
                onClick = { viewModel.startScanning() },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Borrow")
            }
            Spacer(modifier = Modifier.height(8.dp)) // Spacing between buttons
            // Add product button with + icon
            FloatingActionButton(
                onClick = { viewModel.startAddingProduct() }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Product")
            }
            // Ad


            state.scannedProductBarcodeId?.let { scannedProductBarcodeId ->
                if (state.showConfirmationDialog) {
                    BorrowConfirmationDialog(
                        productBarcodeId = scannedProductBarcodeId,
                        onConfirm = { viewModel.confirmBorrowing() },
                        onDismiss = { viewModel.resetDialog() }
                    )
                }
                // Inside your Surface in the CameraScreen composable function
                if (state.showAddProductDialog && state.scannedProductBarcodeId != null) {
                    AddProductDialog(
                        productBarcodeId = state.scannedProductBarcodeId,
                        onConfirm = { productName, productDescription, productCategory, productStatus ->
                            viewModel.submitNewProductDetails(productName, productDescription, productCategory, productStatus)

                        },
                        onDismiss = { viewModel.resetDialog() }
                    )
                }
            }
        }
    }
    LaunchedEffect(key1 = state.message, key2 = state.errorMessage) {
        when {
            state.message != null -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage() // Ensure this method clears both success and error messages.
            }
            state.showError && state.errorMessage.isNotEmpty() -> {
                Toast.makeText(context, state.errorMessage, Toast.LENGTH_LONG).show()
                viewModel.clearMessage() // Use this even if it's named clearError for consistency.
            }
        }
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
fun BorrowConfirmationDialog(productBarcodeId: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Borrow Product") },
        // Include the product ID in the dialog text for clarity
        text = { Text("Do you want to borrow the product ID: $productBarcodeId?") },
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
fun AddProductDialog(productBarcodeId: String, onConfirm: (String, String, String, String) -> Unit, onDismiss: () -> Unit) {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("") }
    var productStatus by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Product") },
        text = {
            Column {
                Text("Scanned Product barcode ID: $productBarcodeId")
                TextField(value = productName, onValueChange = { productName = it }, label = { Text("Product Name") })
                TextField(value = productDescription, onValueChange = { productDescription = it }, label = { Text("Product Description") })
                TextField(value = productCategory, onValueChange = { productCategory = it }, label = { Text("Product Category") })
                TextField(value = productStatus, onValueChange = { productStatus = it }, label = { Text("Product Status") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(productName, productDescription, productCategory, productStatus) }) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
