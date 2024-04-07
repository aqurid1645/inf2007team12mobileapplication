package com.inf2007team12mobileapplication.presentation.notification

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inf2007team12mobileapplication.data.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    application: Application // Injected to access application context
) : AndroidViewModel(application) {
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
                    Log.e("NotificationViewModel", "Error fetching notifications", exception)
                }
        }
    }

    fun sendApprovalNotification(studentUserId: String, reportId: String, productName: String) {
        Firebase.firestore.collection("users").document(studentUserId).get()
            .addOnSuccessListener { documentSnapshot ->
                val studentFcmToken = documentSnapshot.getString("fcmtoken")
                studentFcmToken?.let {
                    val notificationPayload = JSONObject().apply {
                        put("to", it)
                        put("notification", JSONObject().apply {
                            put("title", "Report Approved")
                            put("body", "Your report for $productName has been approved.")
                        })
                        put("data", JSONObject().apply {
                            put("reportId", reportId)
                            put("action", "approve")
                        })
                    }

                    sendNotification(notificationPayload)
                }
            }
    }

    private fun sendNotification(jsonObject: JSONObject) {
        val queue = Volley.newRequestQueue(getApplication())
        val url = "https://fcm.googleapis.com/fcm/send"

        val request = object : JsonObjectRequest(
            Method.POST, url, jsonObject,
            Response.Listener { response ->
                Log.d("NotificationViewModel", "Response: $response")
            },
            Response.ErrorListener { error ->
                Log.e("NotificationViewModel", "Error: ${error.toString()}")
            }) {
            override fun getHeaders(): Map<String, String> = hashMapOf(
                "Authorization" to "key=AAAArmDzDgw:APA91bHSaEAM4Jh6yi3xKoXbbm2ad0Awhbfi9fEEXEd1-TMHxqQJAzheInCR0rI4yUGEWCNAVcMhREnp7TYfXUDB91wEBAkti81L8E_2Ua7Hhg2ODYhoBlq5lstdGNY_HetsfQYz55uj",
                "Content-Type" to "application/json"
            )
        }

        queue.add(request)
    }
}
