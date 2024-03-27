package com.inf2007team12mobileapplication.data

import com.google.firebase.auth.AuthResult
import com.inf2007team12mobileapplication.data.model.Loan
import com.inf2007team12mobileapplication.data.model.Notification
import com.inf2007team12mobileapplication.data.model.Product
import com.inf2007team12mobileapplication.data.model.Report
import kotlinx.coroutines.flow.Flow
interface Repo {
    fun loginUser(email:String,password:String):Flow<Resource<AuthResult>>
    fun registerUser(email:String,password: String):Flow<Resource<AuthResult>>
    fun registerUserWithRole(email: String, password: String, role: String): Flow<Resource<AuthResult>>

    fun getuseremail():String?
    fun signout()
    fun startScanning(): Flow<String?>
    fun getCurrentUserId(): String
    fun checkAndUpdateProductStatus(productId: String): Flow<Resource<String>>
    fun checkResourceAvailability(): Flow<Resource<List<Product>>>
    fun createLoan(loan: Loan)
    fun <T : Any> writeToFirestore(collectionName: String, dataModel: T, documentId: String?= null)
    fun <T : Any> writeToFirestoreflow(collectionName: String, dataModel: T, documentId: String? = null) : Flow<Resource<Unit>>
    fun reportDefectiveProduct(defectReport: Report): Flow<Resource<Unit>>
    fun fetchNotificationsForLecturer(lecturerId: String): Flow<Resource<List<Notification>>>
}