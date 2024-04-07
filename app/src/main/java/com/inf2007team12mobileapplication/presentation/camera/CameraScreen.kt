package com.inf2007team12mobileapplication.presentation.camera

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun CameraScreen(
    navController: NavHostController,
    fromProductCatalog: Boolean,
    viewModel: CameraScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(fromProductCatalog) {
        if (fromProductCatalog) {
            viewModel.startScanning()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose an option to do with Camera",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 64.dp) // Add padding at the bottom
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FloatingActionButton(
                    onClick = { viewModel.startScanning() },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = "Borrow")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Borrow")
                    }
                }

                FloatingActionButton(
                    onClick = { viewModel.startAddingProduct() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Product")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Product")
                    }
                }
            }

            state.scannedProductBarcodeId?.let { scannedProductBarcodeId ->
                if (state.showConfirmationDialog) {
                    BorrowConfirmationDialog(
                        productBarcodeId = scannedProductBarcodeId,
                        onConfirm = { viewModel.confirmBorrowing() },
                        onDismiss = { viewModel.resetDialog() }
                    )
                }

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
                viewModel.clearMessage()
            }
            state.showError && state.errorMessage.isNotEmpty() -> {
                Toast.makeText(context, state.errorMessage, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
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