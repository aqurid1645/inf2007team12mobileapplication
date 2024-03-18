package com.inf2007team12mobileapplication.data

import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
interface Repo {
    fun loginUser(email:String,password:String):Flow<Resource<AuthResult>>
    fun registerUser(email:String,password: String):Flow<Resource<AuthResult>>
    fun getuseremail():String?
    fun signout()
}