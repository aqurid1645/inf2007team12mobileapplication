package com.inf2007team12mobileapplication.presentation.login

data class SignInState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)