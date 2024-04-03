package com.inf2007team12mobileapplication.presentation.homepage

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomePageViewModel @Inject constructor(
    // Assume UserRepository is a dependency that you want to inject.
    // private val userRepository: UserRepository
) : ViewModel() {
    private val db = Firebase.firestore
    var token: String? = null

    fun getToken() {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    try {
                        token = task.result
                        Log.d("token", "" + token)
                        db.collection("users")
                            .document(userId.toString())
                            .update(
                                "fcmtoken", token
                            )
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error updating document", e)
                                if ((e as FirebaseFirestoreException).code
                                    == FirebaseFirestoreException.Code.NOT_FOUND
                                ) {
                                    val data = hashMapOf("fcmtoken" to token)

                                    db.collection("users")
                                        .document(userId.toString())
                                        .set(data)
                                        .addOnSuccessListener {
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(ContentValues.TAG, "Error updating document", e)
                                        }
                                }
                            }

                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // Your ViewModel code here
}

