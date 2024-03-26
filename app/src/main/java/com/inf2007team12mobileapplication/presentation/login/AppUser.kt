package com.inf2007team12mobileapplication.presentation.login

import kotlinx.serialization.Serializable

object AppUser{
    var credential:AppUserCredential? = null
}

@Serializable
data class AppUserCredential(val username: String, val password: String)
