package com.inf2007team12mobileapplication.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "loans")
data class Loan(
    @PrimaryKey val loanId: String,
    val userId: String,
    val resourceId: String,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val status: String
)