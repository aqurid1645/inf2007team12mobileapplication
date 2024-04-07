package com.inf2007team12mobileapplication.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inf2007team12mobileapplication.data.Repo
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(private val repository: Repo) : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _studentId = MutableStateFlow("")
    val studentId: StateFlow<String> = _studentId.asStateFlow()

    fun signout() {
        repository.signout()
    }


    init {
        getCurrentUserId()
    }

    fun getCurrentUserId() {
        viewModelScope.launch {
            _currentUserId.value = repository.getCurrentUserId()
        }
    }
    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            repository.fetchUserProfile(userId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _userProfile.value = resource.data
                    }
                    is Resource.Loading -> {
                        // Optionally handle loading state, e.g., by setting _userProfile to a loading state
                    }
                    is Resource.Error -> {
                        _userProfile.value = null // Set to null or handle error accordingly
                    }
                }
            }
        }
    }


    fun updateUserProfile(userId: String, userProfile: UserProfile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateUserProfile(userId, userProfile).collect { result ->
                when (result) {
                    is Resource.Success -> onSuccess()
                    is Resource.Error -> {
                        // Handle error case
                    }
                    is Resource.Loading -> {
                        // Handle loading case
                    }
                }
            }
        }
    }
    fun updateStudentId(newStudentId: String) {
        _studentId.value = newStudentId
    }

}
