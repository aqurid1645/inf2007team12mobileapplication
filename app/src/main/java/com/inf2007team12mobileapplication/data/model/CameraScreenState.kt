package com.inf2007team12mobileapplication.data.model

data class CameraScreenState(
    val showConfirmationDialog: Boolean = false,
    val scannedProductBarcodeId: String? = null,
    val showError: Boolean = false,
    val errorMessage: String = "",
    val details: String = "",
    val showLoanDetailsDialog: Boolean = false,
    val loanDetails: String? = null,
    val showAddProductDialog: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null,

)

