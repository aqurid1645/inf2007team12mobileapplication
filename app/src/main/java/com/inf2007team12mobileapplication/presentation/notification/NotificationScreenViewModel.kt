package com.inf2007team12mobileapplication.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inf2007team12mobileapplication.data.model.Notification
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            Firebase.firestore.collection("notifications")
                .get()
                .addOnSuccessListener { result ->
                    val notificationsList = result.toObjects(Notification::class.java)
                    _notifications.value = notificationsList
                }
                .addOnFailureListener { exception ->
                    // Handle the error, maybe update the state to show an error message
                }
        }
    }
}
