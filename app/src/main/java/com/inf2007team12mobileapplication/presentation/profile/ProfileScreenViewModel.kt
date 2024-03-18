package com.inf2007team12mobileapplication.presentation.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.inf2007team12mobileapplication.data.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val repository: Repo
) : ViewModel() {
    fun getuseremail():String? {
        return repository.getuseremail()
    }
}