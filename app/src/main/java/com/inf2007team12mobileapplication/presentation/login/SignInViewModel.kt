package com.inf2007team12mobileapplication.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inf2007team12mobileapplication.data.Repo
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: Repo
) : ViewModel() {

    val _signInState = Channel<SignInState>()
    val signInState = _signInState.receiveAsFlow()

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        repository.loginUser(email, password).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _signInState.send(SignInState(isSuccess = "Sign In Success "))
                }
                is Resource.Loading -> {
                    _signInState.send(SignInState(isLoading = true))
                }
                is Resource.Error -> {

                    _signInState.send(SignInState(isError = result.message))
                }
            }

        }
    }
/*    fun checkUserProfileExists(userId: String, onExists: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.checkUserProfileExists(userId).collect { exists ->
                onExists(exists)
            }
        }
    }*/
}