package com.inf2007team12mobileapplication.presentation.camera
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inf2007team12mobileapplication.data.Repo
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.CameraScreenState
import com.inf2007team12mobileapplication.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    private val repository: Repo
) : ViewModel() {
    private val _state = MutableStateFlow(CameraScreenState())
    val state = _state.asStateFlow()

    fun getuseremail(): String? = repository.getuseremail()

    fun startScanning() {
        viewModelScope.launch {
            repository.startScanning().collect { scannedBarcode ->
                if (!scannedBarcode.isNullOrBlank()) {
                    onScanningResult(scannedBarcode)
                } else {
                    updateStateForError("Scanning failed or no barcode detected.")
                }
            }
        }
    }

    private fun onScanningResult(scannedProductId: String) {
        _state.value = _state.value.copy(
            showConfirmationDialog = true,
            scannedProductBarcodeId = scannedProductId
        )
    }
    // Streamlined to reduce redundancy and focus on functionality.
    fun startAddingProduct() {
        // First check the user role
        viewModelScope.launch {
            repository.fetchUserRole().collect { roleResource ->
                when (roleResource) {
                    is Resource.Success -> {
                        if (roleResource.data == "lecturer") {
                            // If the user is a lecturer, proceed with scanning
                            initiateScanningForProductAddition()
                        } else {
                            // If the user is not a lecturer, show an error message
                            _state.value = _state.value.copy(
                                message = "You do not have permission to add products."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            errorMessage = roleResource.message ?: "Failed to fetch user role.",
                            showError = true
                        )
                    }
                    is Resource.Loading -> {
                        // Optionally handle loading state
                    }
                }
            }
        }
    }
    private fun initiateScanningForProductAddition() {
        viewModelScope.launch {
            repository.startScanning().collect { scannedBarcode ->
                if (!scannedBarcode.isNullOrBlank()) {
                    _state.value = _state.value.copy(
                        showAddProductDialog = true,
                        scannedProductBarcodeId = scannedBarcode
                    )
                } else {
                    updateStateForError("Scanning failed or no barcode detected.")
                }
            }
        }
    }


    // Enhanced for clarity and error handling.
    fun submitNewProductDetails(productName: String, productDescription: String, productCategory: String, status: String) {
        _state.value.scannedProductBarcodeId?.let { barcodeId ->
            addProduct(Product(UUID.randomUUID().toString(), productName, productDescription, productCategory, status, barcodeId))
        } ?: updateStateForError("No barcode scanned.")
    }

    // Refactored to handle loading and message updates more effectively.
    fun addProduct(product: Product) {

        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            repository.writeToFirestoreflow("products", product, null).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // On success, update the state to hide the dialog and show a success message
                        _state.value = _state.value.copy(
                            showAddProductDialog = false, // Hide the add product dialog
                            isLoading = false, // Loading is done
                            message = "Product added successfully." // Show success message
                        )
                    }
                    is Resource.Error -> {
                        // On error, update the state to show an error message and stop loading
                        _state.value = _state.value.copy(
                            isLoading = false, // Loading is done
                            errorMessage = "Failed to add product.", // Show error message
                            showError = true // Indicate that there's an error
                        )
                    }
                    is Resource.Loading -> {
                        // Explicit handling of the loading state is not necessary here since it's set at the start
                    }
                }
            }
        }
    }

    fun confirmBorrowing() {
        val productId = _state.value.scannedProductBarcodeId
        if (productId != null) {
            viewModelScope.launch {
                repository.checkAndUpdateProductStatus(productId).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            // Update UI to show loading message
                            updateStateForLoading("Checking product status...")
                        }
                        is Resource.Success -> {
                            val loanDetails = resource.data
                            _state.value = _state.value.copy(
                                showLoanDetailsDialog = true,
                                loanDetails = loanDetails
                            )
                            updateStateForSuccess("Product ID $productId borrowed successfully.")
                        }
                        is Resource.Error -> {
                            updateStateForError( "Failed to borrow product ID $productId.")
                        }
                    }
                }
            }
        } else {
            // Handle the case where no product ID was scanned
            updateStateForError("No product ID scanned.")
        }
    }
    // Consolidated loading, success, and error state updates.
    private fun updateStateForLoading(message: String) {
        _state.value = _state.value.copy(isLoading = true, details = message)
    }

    private fun updateStateForSuccess(message: String) {
        _state.value = _state.value.copy(isLoading = false, message = message, showError = false, errorMessage = "")
    }

    private fun updateStateForError(errorMessage: String) {
        _state.value = _state.value.copy(isLoading = false, message = null, showError = true, errorMessage = errorMessage)
    }

    // Enhanced reset functionality to clear both messages and errors.
    fun resetDialog() {
        _state.value = _state.value.copy(showConfirmationDialog = false, showLoanDetailsDialog = false, loanDetails = null, scannedProductBarcodeId = null, showAddProductDialog = false, message = null, errorMessage = "", showError = false)
    }

    // Added for explicitly clearing messages.
    fun clearMessage() {
        _state.value = _state.value.copy(message = null, errorMessage = "", showError = false)
    }
    fun setFromProductCatalog(fromProductCatalog: Boolean) {
        if (fromProductCatalog) {
            startScanning()
        }
    }
}
