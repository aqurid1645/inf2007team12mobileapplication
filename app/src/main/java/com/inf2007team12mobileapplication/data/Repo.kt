package com.inf2007team12mobileapplication.data

import com.google.firebase.auth.AuthResult
import com.inf2007team12mobileapplication.data.model.DefectReport
import com.inf2007team12mobileapplication.data.model.Loan
import com.inf2007team12mobileapplication.data.model.Notification
import com.inf2007team12mobileapplication.data.model.Product
import kotlinx.coroutines.flow.Flow
interface Repo {
    fun loginUser(email:String,password:String):Flow<Resource<AuthResult>>
    fun registerUser(email:String,password: String):Flow<Resource<AuthResult>>
    fun registerUserWithRole(email: String, password: String, role: String): Flow<Resource<AuthResult>>

    fun getuseremail():String?
    fun signout()
    fun startScanning(): Flow<String?>

    fun checkAndUpdateProductStatus(productId: String): Flow<Resource<Boolean>>
    fun checkResourceAvailability(): Flow<Resource<List<Product>>>
    fun createLoan(loan: Loan): Flow<Resource<Unit>>
    fun reportDefectiveProduct(defectReport: DefectReport): Flow<Resource<Unit>>
    fun fetchNotificationsForLecturer(lecturerId: String): Flow<Resource<List<Notification>>>
}