package com.inf2007team12mobileapplication.presentation.biometric

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import java.util.concurrent.Executors

class BiometricAuthenticator constructor(activity: AppCompatActivity) {
    val activity: AppCompatActivity = activity

    fun canAuthenticate(): Boolean {
        val biometricManager: BiometricManager = BiometricManager.from(activity)
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun createBiometricPrompt(
        processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit
    ): BiometricPrompt {
        val executor = Executors.newSingleThreadExecutor()
        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errCode, errString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                processSuccess(result)
            }
        }
        return BiometricPrompt(activity, executor, callback)
    }

    fun createPromptInfo(): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle("Authenticate")
            setSubtitle("FingerPrint Authentication")
            setDescription("Please place your finger on the sensor")
            setConfirmationRequired(false)
            setNegativeButtonText("Cancel")
        }.build()
}