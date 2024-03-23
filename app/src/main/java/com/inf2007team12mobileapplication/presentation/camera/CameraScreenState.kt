package com.inf2007team12mobileapplication.presentation.camera

data class CameraScreenState(
    val showConfirmationDialog: Boolean = false,
    val scannedProductId: String? = null,
    val scannedProductName: String? = null, // Add this line
    val showError: Boolean = false,
    val errorMessage: String = "",
    val details: String = ""
)

