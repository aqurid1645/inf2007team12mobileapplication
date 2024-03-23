package com.inf2007team12mobileapplication.data.model

import java.util.Date

data class Loan(
    val loanId: String = "",
    val userId: String = "",
    val resourceId: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val status: String = ""
)