package com.inf2007team12mobileapplication.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Database(entities = [Loan::class], version = 1, exportSchema = false)
@TypeConverters(TimestampConverter::class)
abstract class LoanRoomDatabase : RoomDatabase() {
    abstract fun loanDao(): LoanDao

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    companion object {
        @Volatile
        private var INSTANCE: LoanRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LoanRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LoanRoomDatabase::class.java,
                    "loan_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(LoanDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class LoanDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid // Get current user ID directly
                        if (userId != null) {
                            fetchAndInsertUserLoans(database.loanDao(), userId)
                        }
                    }
                }
            }
        }

        suspend fun fetchAndInsertUserLoans(loanDao: LoanDao, userId: String) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("loans")
                .whereEqualTo("userId", userId)
                .get()
                .await() // Use await() to wait for the query to complete
                .toObjects(Loan::class.java) // Convert query snapshot to a list of Loan objects
                .also { loans ->
                    withContext(Dispatchers.IO) {
                        loans.forEach { loan ->
                            loanDao.insertLoan(loan) // Insert each Loan into the Room database
                        }
                    }
                }
        }
    }
}
