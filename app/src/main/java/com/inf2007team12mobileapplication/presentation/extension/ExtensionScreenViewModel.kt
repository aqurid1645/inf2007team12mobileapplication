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

    // Updated part: Add a flow to hold the list of loans
    private val _loans = MutableStateFlow<List<Loan>>(emptyList())
    val loans = _loans.asStateFlow()

    fun fetchCurrentUserLoans() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = repository.getCurrentUserId()
            if (userId.isNotEmpty()) {
                try {
                    val loans = repository.getUserLoans(userId)
                    _loans.value = loans // Post value to the flow
                } catch (e: Exception) {
                    Log.e("ViewModelError", "Error fetching loans: ${e.localizedMessage}")
                    // Consider sending an error status through a StateFlow or Channel
                }
            } else {
                // Handle case where user ID is not found (e.g., user not logged in)
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
