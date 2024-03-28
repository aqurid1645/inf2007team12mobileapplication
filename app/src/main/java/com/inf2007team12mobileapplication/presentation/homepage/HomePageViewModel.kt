package com.inf2007team12mobileapplication.presentation.homepage

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    // Assume UserRepository is a dependency that you want to inject.
    // private val userRepository: UserRepository
) : ViewModel() {
    // Your ViewModel code here
}

