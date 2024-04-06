package com.inf2007team12mobileapplication.presentation.homepage

import com.inf2007team12mobileapplication.data.Repo
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.inf2007team12mobileapplication.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val repo: Repo // Inject Repo implementation
) : ViewModel() {
    private val _tokenUpdateStatus = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val tokenUpdateStatus: StateFlow<Resource<Unit>> = _tokenUpdateStatus

    init {
        getToken()
    }

    private fun getToken() {
        viewModelScope.launch {
            repo.getToken().collect { status ->
                _tokenUpdateStatus.value = status
            }
        }
    }
}