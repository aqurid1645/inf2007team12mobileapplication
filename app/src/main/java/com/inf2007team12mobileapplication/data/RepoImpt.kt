package com.inf2007team12mobileapplication.data

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RepoImpt@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val scanner: GmsBarcodeScanner,
    private val firestore: FirebaseFirestore
) : Repo {


    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun registerUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }    }
    override fun getuseremail(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.email
    }

    override fun signout() {
        firebaseAuth.signOut()
    }
    override fun startScanning(): Flow<String?> {
        return callbackFlow {
            scanner.startScan()
                .addOnSuccessListener {
                    launch {
                        send(getDetails(it))
                    }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            awaitClose { }
        }

    }

    override fun registerUserWithRole(email: String, password: String, role: String): Flow<Resource<AuthResult>> {
        return flow {
            // Validate email format
            if (!email.endsWith("@sit.edu.sg") && !email.endsWith("@sit.university.edu.sg")) {
                emit(Resource.Error("Please follow the email format: @sit.edu.sg or @sit.university.edu.sg"))
                return@flow // Early return if email format is incorrect
            }

            emit(Resource.Loading())
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

                // Determine the role based on the email domain, you might not need the 'role' parameter anymore.
                val isLecturer = email.endsWith("@sit.university.edu.sg")
                val determinedRole = if (isLecturer) "lecturer" else "student"

                val userProfile = mapOf("role" to determinedRole)
                firestore.collection("users").document(result.user!!.uid)
                    .set(userProfile).await()

                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }


    private fun getDetails(barcode: Barcode): String {
        return when (barcode.valueType) {
            Barcode.TYPE_WIFI -> {
                val ssid = barcode.wifi!!.ssid
                val password = barcode.wifi!!.password
                val type = barcode.wifi!!.encryptionType
                "ssid : $ssid, password : $password, type : $type"
            }
            Barcode.TYPE_URL -> {
                "url : ${barcode.url!!.url}"
            }
            Barcode.TYPE_PRODUCT -> {
                "productType : ${barcode.displayValue}"
            }
            Barcode.TYPE_EMAIL -> {
                "email : ${barcode.email}"
            }
            Barcode.TYPE_CONTACT_INFO -> {
                "contact : ${barcode.contactInfo}"
            }
            Barcode.TYPE_PHONE -> {
                "phone : ${barcode.phone}"
            }
            Barcode.TYPE_CALENDAR_EVENT -> {
                "calender event : ${barcode.calendarEvent}"
            }
            Barcode.TYPE_GEO -> {
                "geo point : ${barcode.geoPoint}"
            }
            Barcode.TYPE_ISBN -> {
                "isbn : ${barcode.displayValue}"
            }
            Barcode.TYPE_DRIVER_LICENSE -> {
                "driving license : ${barcode.driverLicense}"
            }
            Barcode.TYPE_SMS -> {
                "sms : ${barcode.sms}"
            }
            Barcode.TYPE_TEXT -> {
                "text : ${barcode.rawValue}"
            }
            Barcode.TYPE_UNKNOWN -> {
                "unknown : ${barcode.rawValue}"
            }
            else -> {
                "Couldn't determine"
            }
        }

    }
}