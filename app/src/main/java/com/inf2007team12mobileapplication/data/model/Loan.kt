package com.inf2007team12mobileapplication.data.model

import com.google.firebase.Timestamp

data class Loan(
    val loanId: String = "",
    val userId: String = "",
    val resourceId: String = "",
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val status: String = ""
)
