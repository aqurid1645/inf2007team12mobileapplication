package com.inf2007team12mobileapplication.presentation.camera
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inf2007team12mobileapplication.data.Repo
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
            scannedProductId = scannedProductId
        )
    }
    fun addproducts(product: Product){
        viewModelScope.launch{
            repository.writeToFirestore("products", product,null)
        }
    }
    fun confirmBorrowing() {
        val productId = _state.value.scannedProductId
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
                            // Update UI for error
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
    private fun updateStateForLoading(message: String) {
        _state.value = _state.value.copy(
            details = message,
            showConfirmationDialog = false, // Assuming you may want to hide the dialog during loading
            scannedProductId = _state.value.scannedProductId, // Keep the scanned product ID
            showError = false, // Reset the error state
            errorMessage = "" // Clear any existing error messages
        )
    }

    private fun updateStateForSuccess(message: String) {
        _state.value = _state.value.copy(details = message, showConfirmationDialog = false, scannedProductId = null, showError = false)
    }

    private fun updateStateForError(errorMessage: String) {
        _state.value = _state.value.copy(errorMessage = errorMessage, showError = true, showConfirmationDialog = false)
    }

    fun resetDialog() {
        _state.value = _state.value.copy(
            showConfirmationDialog = false,
            showLoanDetailsDialog = false,
            loanDetails = null,
            scannedProductId = null
        )
    }


    fun clearError() {
        _state.value = _state.value.copy(showError = false, errorMessage = "")
    }
}
