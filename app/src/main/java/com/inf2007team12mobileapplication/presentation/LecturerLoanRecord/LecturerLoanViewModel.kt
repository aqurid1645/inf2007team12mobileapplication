package com.inf2007team12mobileapplication.presentation.LecturerLoanRecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.google.firebase.Timestamp
import com.inf2007team12mobileapplication.data.RepoImpt
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.Loan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class LecturerLoanViewModel @Inject constructor(
    private val repository: RepoImpt
) : ViewModel() {

    private val TAG = "LecturerLoanViewModel"

    private val _allLoans = MutableStateFlow<Resource<List<Loan>>>(Resource.Loading())
    private val _filteredLoans = MutableStateFlow<Resource<List<Loan>>>(Resource.Loading())
    val loans: StateFlow<Resource<List<Loan>>> = _filteredLoans

    fun fetchLoansByStudentId(studentId: String) {
        viewModelScope.launch {
            repository.fetchLoansByStudentID(studentId).collect { resource ->
                _allLoans.value = resource
                _filteredLoans.value = resource // Initially, filtered loans are all loans
            }
        }
    }

    fun applyFilters(filterStartDate: String?, filterEndDate: String?, productName: String?, filterMonth: String?, filterYear: Int?) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val parsedFilterStartDate = parseDateStringToTimestamp(filterStartDate, dateFormat)
        val parsedFilterEndDate = if (filterEndDate.isNullOrBlank()) null else parseDateStringToTimestamp(filterEndDate, dateFormat)
        Log.d(TAG, "Parsed Start Date: $parsedFilterStartDate")
        Log.d(TAG, "Parsed End Date: $parsedFilterEndDate")

        val filteredLoans = when (val result = _allLoans.value) {
            is Resource.Success -> {
                val filtered = result.data?.filterNotNull()?.filter { loan ->
                    val matchesDateRange = matchesDateRange(loan, parsedFilterStartDate, parsedFilterEndDate)
                    val matchesProductName = productName.isNullOrBlank() || loan.productName.contains(productName, ignoreCase = true)
                    val matchesMonthAndYear = filterMonthAndYear(loan.startDate, filterMonth, filterYear) || filterMonthAndYear(loan.endDate, filterMonth, filterYear)

                    matchesDateRange && matchesProductName && matchesMonthAndYear
                }

                Resource.Success(filtered)
            }
            else -> result
        }

        Log.d(TAG, "Filtered Loans: $filteredLoans")

        _filteredLoans.value = filteredLoans as Resource<List<Loan>>
    }

    private fun parseDateStringToTimestamp(dateString: String?, dateFormat: SimpleDateFormat): Timestamp? =
        dateString?.let {
            if (it.isNotEmpty()) {
                try {
                    val parsedDate = dateFormat.parse(it)
                    Log.d(TAG, "Parsed Date: $parsedDate")
                    parsedDate?.let { date -> Timestamp(date) }
                } catch (e: ParseException) {
                    Log.e(TAG, "Error parsing date: $dateString", e)
                    null
                }
            } else {
                null
            }
        }


    private fun matchesDateRange(loan: Loan, start: Timestamp?, end: Timestamp?): Boolean =
        when {
            start != null && end != null ->
                (loan.startDate?.compareTo(start) ?: 0 >= 0) && (loan.endDate?.compareTo(end) ?: 0 <= 0)
            else -> true
        }

    private fun filterMonthAndYear(date: Timestamp?, filterMonth: String?, filterYear: Int?): Boolean {
        if (date == null || filterMonth == null || filterYear == null) return true // Ignore filter if any parameter is null

        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("UTC")
            time = date.toDate()
        }

        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is zero-based
        val year = calendar.get(Calendar.YEAR)
        val monthInt = filterMonth.toIntOrNull() ?: return false // Safely convert string month to Int

        return month == monthInt && year == filterYear
    }
}
