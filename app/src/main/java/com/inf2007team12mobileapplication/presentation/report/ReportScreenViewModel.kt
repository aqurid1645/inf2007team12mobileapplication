package com.inf2007team12mobileapplication.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inf2007team12mobileapplication.data.Repo
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.Loan
import com.inf2007team12mobileapplication.data.model.Report
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportScreenViewModel @Inject constructor(
    private val repository: Repo
) : ViewModel() {
    private val _reportStatus = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val reportStatus: StateFlow<Resource<Unit>> = _reportStatus.asStateFlow()

    private val _loans = MutableStateFlow<Resource<List<Loan>>>(Resource.Loading())
    val loans: StateFlow<Resource<List<Loan>>> = _loans.asStateFlow()

    private val _submissionStatus = MutableStateFlow<Resource<String>?>(null)
    val submissionStatus: StateFlow<Resource<String>?> = _submissionStatus.asStateFlow()

    fun getCurrentUserId(): String {
        return repository.getCurrentUserId()
    }

    init {
        fetchUserLoans()
    }

    fun fetchUserLoans() {
        val userId = getCurrentUserId()
        viewModelScope.launch {
            repository.getUserLoans(userId).collect { resource ->
                _loans.value = resource
            }
        }
    }

    fun reportDefectiveProduct(defectReport: Report) = viewModelScope.launch {
        repository.reportDefectiveProduct(defectReport).collect { resource ->
            when (resource) {
                is Resource.Success -> _submissionStatus.value = Resource.Success("Report submitted successfully")
                is Resource.Error -> _submissionStatus.value = Resource.Error(resource.message ?: "Error submitting report")
                else -> {   _submissionStatus.value = Resource.Loading(resource.message ?: "Not available at the moment")
                 }
            }
            _reportStatus.value = resource
        }
    }

    fun clearSubmissionStatus() {
        _submissionStatus.value = null // Clear the submission status message
    }
}
