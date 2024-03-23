package com.inf2007team12mobileapplication.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: Loan)

    @Query("SELECT * FROM loans WHERE loanId = :loanId")
    fun getLoanById(loanId: String): Flow<Loan>
    @Query("DELETE FROM loans")
    suspend fun deleteALL()
    @Query("SELECT * FROM loans")
    fun getAllLoans(): Flow<List<Loan>>

}