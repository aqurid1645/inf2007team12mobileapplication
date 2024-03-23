package com.inf2007team12mobileapplication.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.inf2007team12mobileapplication.data.model.DefectReport
import com.inf2007team12mobileapplication.data.model.Loan
import com.inf2007team12mobileapplication.data.model.Notification
import com.inf2007team12mobileapplication.data.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
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
        }
    }

    override fun getuseremail(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.email
    }

    override fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
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

    override fun registerUserWithRole(
        email: String,
        password: String,
        role: String
    ): Flow<Resource<AuthResult>> {
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
                "${barcode.displayValue}"
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

    override fun checkAndUpdateProductStatus(productId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())


        val querySnapshot = firestore.collection("Product")
            .whereEqualTo("productbarcodeID", productId)
            .get()
            .await()
        Log.d("ProductStatus", "Loading started for product ID: $productId")
        Log.d("ProductStatus", "Query completed. Found ${querySnapshot.size()} documents.")

        // Check if the query returned any documents
        if (querySnapshot.documents.isEmpty()) {
            Log.d("ProductStatus", "No documents found for productBarcodeID: $productId")
            emit(Resource.Error("Product does not exist."))
            return@flow
        }

        val documentSnapshot = querySnapshot.documents.first()
        val product = documentSnapshot.toObject(Product::class.java)

        product?.let {
            Log.d("ProductStatusDebug", "Product current status: ${it.status}")
            when (it.status) {
                "loaned" -> {
                    Log.d("ProductStatusDebug", "Product has already been borrowed.")
                    emit(Resource.Error("Product has already been borrowed."))
                }
                "reserved" -> {
                    Log.d("ProductStatusDebug", "Product is reserved and cannot be borrowed at this time.")
                    emit(Resource.Error("Product is reserved and cannot be borrowed at this time."))
                }
                "available" -> {
                    Log.d("ProductStatusDebug", "Attempting to update product status to loaned for document ID: ${documentSnapshot.id}")
                    // Update the status to "loaned" if the product is currently available
                    firestore.collection("Product").document(documentSnapshot.id)
                        .update("status", "loaned").await()


                    var newLoan = Loan(
                        loanId = UUID.randomUUID().toString(),
                        userId = getCurrentUserId(),
                        resourceId = product.resourceId,
                        startDate = Timestamp(Date()),
                        endDate = Timestamp(Date()),
                        status = "active"
                    )

                    createLoan(newLoan)

                    // Log a success message after the loan is created
                    Log.d("CreateLoan", "Loan created successfully.")
                    emit(Resource.Success(true))
                }
                else -> {
                    Log.d("ProductStatusDebug", "Unhandled product status: ${it.status}")
                    emit(Resource.Error("Unhandled product status."))
                }
            }
        } ?: emit(Resource.Error("Error retrieving product information."))
    }.catch { e ->
        Log.e("ProductStatusDebug", "An error occurred in checkAndUpdateProductStatus", e)
        emit(Resource.Error(e.message ?: "An unknown error occurred"))
    }


    // Function to check resource availability
    override fun checkResourceAvailability(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())

        val snapshot = firestore.collection("Product")
            .whereEqualTo("status", "available")
            .get()
            .await()

        val products = snapshot.toObjects(Product::class.java)
        emit(Resource.Success(products))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred while fetching available products."))
    }

    // Function to initiate a new loan
    override fun createLoan(loan: Loan): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        firestore.collection("loans").document().set(loan)
        emit(Resource.Success(Unit))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred while creating the loan."))
    }


    // Function to report a defective product
    override fun reportDefectiveProduct(defectReport: DefectReport): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        firestore.collection("defectReports").add(defectReport).await()
        emit(Resource.Success(Unit)) // Emit success with Unit value
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred while reporting the defect."))
    }
    // Function to fetch notifications for a lecturer
    override fun fetchNotificationsForLecturer(lecturerId: String): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading())

        val snapshot = firestore.collection("notifications")
            .whereEqualTo("userId", lecturerId)
            .get()
            .await()

        val notifications = snapshot.toObjects(Notification::class.java)
        emit(Resource.Success(notifications))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred while fetching notifications."))
    }


}