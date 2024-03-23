package com.inf2007team12mobileapplication.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inf2007team12mobileapplication.data.Repo
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.presentation.login.SignInState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: Repo
) : ViewModel() {

    val _signUpState  = Channel<SignInState>()
    val signUpState  = _signUpState.receiveAsFlow()

    fun registerUser(email:String, password:String) = viewModelScope.launch {
        repository.registerUser(email, password).collect{result ->
            when(result){
                is Resource.Success ->{
                    _signUpState.send(SignInState(isSuccess = "Sign Up Success "))
                }
                is Resource.Loading ->{
                    _signUpState.send(SignInState(isLoading = true))
                }
                is Resource.Error ->{

                    _signUpState.send(SignInState(isError = result.message))
                }
            }

        }
    }
    fun registerUserwithrole(email: String, password: String) = viewModelScope.launch {
        val role = if (email.contains("@sit.edu.sg")) "student" else "lecturer" // Simplified role determination logic
        repository.registerUserWithRole(email, password, role).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _signUpState.send(SignInState(isSuccess = "Sign Up Success "))
                }
                is Resource.Loading -> {
                    _signUpState.send(SignInState(isLoading = true))
                }
                is Resource.Error -> {
                    _signUpState.send(SignInState(isError = result.message))
                }
            }
        }
    }

}