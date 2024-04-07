package com.inf2007team12mobileapplication.presentation.extension

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.inf2007team12mobileapplication.data.Repo
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.Loan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtensionScreenViewModel @Inject constructor(
    private val repository: Repo
) : ViewModel() {

    private val _loanDuplicationChannel = Channel<Resource<String>>()
    val loanDuplicationFlow = _loanDuplicationChannel.receiveAsFlow()
    private val _loans = MutableStateFlow<Resource<List<Loan>>>(Resource.Loading())

    val loans = _loans.asStateFlow()
    fun getCurrentUserId(): String {
        return repository.getCurrentUserId()
    }

    fun fetchCurrentUserLoans() {
        val userId = getCurrentUserId()
        viewModelScope.launch {
            repository.getUserLoans(userId).collect { resource ->
                _loans.value = resource
            }
        }
    }

    fun extendLoanEndDate(loanId: String, currentEndDate: Timestamp) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.createDuplicateLoanWithNewEndDate(loanId, currentEndDate)
                // Ensure this is called and successfully fetches and updates the loans list
                fetchCurrentUserLoans()
                _loanDuplicationChannel.send(Resource.Success("Loan extended successfully"))
            } catch (e: Exception) {
                _loanDuplicationChannel.send(Resource.Error(e.localizedMessage ?: "Error extending loan"))
            }
        }
    }
}