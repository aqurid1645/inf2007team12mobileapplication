package com.inf2007team12mobileapplication.data.room

import android.app.Application
import kotlinx.coroutines.GlobalScope



class LoanApp : Application() {
    // Initialize your database and repository for Loan
    val loanDao by lazy { LoanRoomDatabase.getDatabase(this, GlobalScope).loanDao() }
    val repository by lazy { LoanRepository(loanDao) }
}
