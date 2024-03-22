package com.inf2007team12mobileapplication.presentation.profile

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inf2007team12mobileapplication.data.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(private val repository: Repo) : ViewModel() {
    private val db = Firebase.firestore
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    fun signout() {
        repository.signout()
    }
    fun getUserProfile(userId: String): LiveData<UserProfile> {
        val userProfileLiveData = MutableLiveData<UserProfile>()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                userProfile?.let { userProfileLiveData.value = it }
            }
            .addOnFailureListener { e ->
                // Handle error
                Log.e("ProfileViewModel", "Error fetching user profile", e)
            }
        return userProfileLiveData
    }

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    _userProfile.value = documentSnapshot.toObject(UserProfile::class.java)
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileViewModel", "Error fetching user profile", e)
                }
        }
    }


    fun updateUserProfile(userId: String, userProfile: UserProfile, onSuccess: () -> Unit) {
        db.collection("users").document(userId).set(userProfile)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }

    fun isContactNumberTaken(userId: String, contactNumber: String, onResult: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("contactNumber", contactNumber)
            .get()
            .addOnSuccessListener { documents ->
                val isTaken = documents.any { document -> document.id != userId }
                onResult(isTaken)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error checking if contact number is taken", e)
                onResult(false) // or handle error as needed
            }
    }
}
