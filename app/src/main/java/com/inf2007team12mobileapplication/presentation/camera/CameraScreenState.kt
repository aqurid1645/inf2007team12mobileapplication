package com.inf2007team12mobileapplication.presentation.camera

data class CameraScreenState(
    val details: String = "",
    val showConfirmationDialog: Boolean = false,
    val scannedProductId: String? = null,
    val showError: Boolean = false,
    val errorMessage: String = ""
)
