package com.inf2007team12mobileapplication.presentation.report

import android.app.Application
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inf2007team12mobileapplication.data.Repo
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.Loan
import com.inf2007team12mobileapplication.data.model.Report
import com.inf2007team12mobileapplication.data.model.UserProfile
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportScreenViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: Repo, application: Application
) : AndroidViewModel(application) {

    private val _reportStatus = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val reportStatus: StateFlow<Resource<Unit>> = _reportStatus.asStateFlow()

    private val _loans = MutableStateFlow<Resource<List<Loan>>>(Resource.Loading())
    val loans: StateFlow<Resource<List<Loan>>> = _loans.asStateFlow()

    private val _submissionStatus = MutableStateFlow<Resource<String>?>(null)
    val submissionStatus: StateFlow<Resource<String>?> = _submissionStatus.asStateFlow()

    private val db = Firebase.firestore

    fun getCurrentUserId(): String {
        return repository.getCurrentUserId()
    }

    init {
        fetchUserLoans()
    }

    fun Send_Notification(jsonObject: JSONObject?) {
        VolleyLog.DEBUG = true;
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            "https://fcm.googleapis.com/fcm/send", jsonObject,
            Response.Listener { response ->
                Log.d("Result error", response.toString())
                _submissionStatus.value = Resource.Success("Report submitted successfully")
            }, Response.ErrorListener { error ->
                Log.d("Result error", error.message.toString())
                _submissionStatus.value = Resource.Success("Report submitted successfully")
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                //FCM Key Token Get From Firebase Console
                //Replace This AAAAbmbafO4:APA91bGi4mUakFqfmgnoqiC6MSkBqlgzGmB5M7Z4hqY1dOqPZnsegby9PDrpTHF9l2KUJgC7Q4yeclE8VpnGQq6wkvm5Zxemh2hmyAsiSE5YTl6TImy3Az1LkGvIZ_zylFVXE9fsw5aF
                headers["Authorization"] =
                    "key=AAAArmDzDgw:APA91bHSaEAM4Jh6yi3xKoXbbm2ad0Awhbfi9fEEXEd1-TMHxqQJAzheInCR0rI4yUGEWCNAVcMhREnp7TYfXUDB91wEBAkti81L8E_2Ua7Hhg2ODYhoBlq5lstdGNY_HetsfQYz55uj"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        val requestQueue = Volley.newRequestQueue(getApplication())
        jsonObjReq.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.cache.clear()
        requestQueue.add(jsonObjReq)
    }

//    fun fetchUserLoans() {
//        val userId = getCurrentUserId()
//        viewModelScope.launch {
//            repository.getUserLoans(userId).collect { resource ->
//                _loans.value = resource
//            }
//        }
//    }

//    weiwen
//    Mock fetchUserLoans
    fun fetchUserLoans() {
        viewModelScope.launch {
            val mockLoans = List(10) { index -> // Generate 10 Loan objects
                Loan(
                    loanId = UUID.randomUUID().toString(), // Generate unique ID
                    productName = "Mock Product ${index + 1}",
                    productBarcodeID = (12345 + index).toString() // Just an example to differentiate productBarcodeID
                )
            }
            _loans.value = Resource.Success(mockLoans)
        }
    }


    fun reportDefectiveProduct(defectReport: Report) = viewModelScope.launch {
        repository.reportDefectiveProduct(defectReport).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    _submissionStatus.value = Resource.Success("Report submitted successfully")
                    db.collection("users")
                        .whereEqualTo("role", "lecturer").get()
                        .addOnSuccessListener { result ->
                            if (result != null) {
                                val jsonaa = JSONArray();
                                // PUT all the fcm token of lecturer and send notification
                                for (document in result) {
                                    if (document.get("fcmtoken") != null) {
                                        jsonaa.put(""+document.get("fcmtoken").toString())
                                    }
                                }
                                val json = JSONObject()
                                try {
                                    json.put("registration_ids",jsonaa)
                                    val info = JSONObject()
                                    info.put("title", ""
                                            + defectReport.productName)
                                    info.put(
                                        "body",
                                        "" + defectReport.description
                                    )
                                    json.put("notification", info)
                                    json.put("data", info)
                                    Send_Notification(json)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            } else {
                            }
                        }
                        .addOnFailureListener { e ->
                            // Handle error
                            Log.e("ProfileViewModel", "Error fetching user profile", e)
                        }
                }
                is Resource.Error -> {
                    _submissionStatus.value =
                        Resource.Error(resource.message ?: "Error submitting report")
                }
                else -> {   _submissionStatus.value = Resource.Loading(resource.message ?: "Not available at the moment")
                }
            }
            _reportStatus.value = resource
        }
    }

    fun clearSubmissionStatus() {
        _submissionStatus.value = null // Clear the submission status message
    }
}
