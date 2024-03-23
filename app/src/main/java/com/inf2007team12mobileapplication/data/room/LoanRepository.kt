package com.inf2007team12mobileapplication.data.room

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class LoanRepository(private val loanDao: LoanDao) {

    val allLoans: Flow<List<Loan>> = loanDao.getAllLoans()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(loan: Loan) {
        loanDao.insertLoan(loan)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        loanDao.deleteALL()
    }
}

