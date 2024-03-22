package com.inf2007team12mobileapplication.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser

import com.inf2007team12mobileapplication.data.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val repository: Repo
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileScreenState())
    val state = _state.asStateFlow()

    fun getuseremail(): String? {
        return repository.getuseremail()
    }


    fun startScanning() {
        viewModelScope.launch {
            repository.startScanning().collect {
                if (!it.isNullOrBlank()) {
                    _state.value = state.value.copy(
                        details = it
                    )
                }
            }
        }
    }
}