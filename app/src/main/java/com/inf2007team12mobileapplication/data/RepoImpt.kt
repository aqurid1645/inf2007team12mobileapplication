package com.inf2007team12mobileapplication.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.inf2007team12mobileapplication.data.model.Loan
import com.inf2007team12mobileapplication.data.model.Notification
import com.inf2007team12mobileapplication.data.model.Product
import com.inf2007team12mobileapplication.data.model.Report
import com.inf2007team12mobileapplication.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import kotlin.coroutines.suspendCoroutine

class RepoImpt@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val scanner: GmsBarcodeScanner,
    private val firestore: FirebaseFirestore

) : Repo {

    fun fetchProducts(): Flow<List<Product>> = callbackFlow {
        val listenerRegistration = firestore.collection("products")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e) // Close the flow with an error
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { it.toObject(Product::class.java) }.orEmpty()
                trySend(products) // Send the products list to the flow
            }
        awaitClose { listenerRegistration.remove() } // Remove the listener when the flow collector is done
    }


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

    override fun fetchLoansByStudentID(studentId: String): Flow<Resource<List<Loan>>> = flow {
        Log.d("LoanSearch", "Fetching loans for student ID: $studentId")
        emit(Resource.Loading())

        try {
            // First, find the user ID based on student ID
            val userQuerySnapshot = firestore.collection("users")
                .whereEqualTo("studentID", studentId)
                .get()
                .await()
            Log.d("LoanSearch", "User query completed. Found: ${userQuerySnapshot.documents.size} users")

            if (userQuerySnapshot.documents.isNotEmpty()) {
                val userId = userQuerySnapshot.documents.first().id
                Log.d("LoanSearch", "User ID: $userId found, fetching loans...")

                // Now, fetch loans based on user ID
                val loansSnapshot = firestore.collection("loans")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                val loans = loansSnapshot.toObjects(Loan::class.java)
                Log.d("LoanSearch", "Loans fetch completed. Found: ${loans.size} loans")

                emit(Resource.Success(loans))
            } else {
                Log.d("LoanSearch", "No user found with the provided student ID.")
                emit(Resource.Error("No user found with the provided student ID."))
            }
        } catch (e: Exception) {
            Log.e("LoanSearch", "Failed to fetch loans: ${e.message}", e)
            emit(Resource.Error("Failed to fetch loans: ${e.message}"))
        }
    }



    override fun fetchUserRole(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        val userId = getCurrentUserId()
        if (userId.isBlank()) {
            emit(Resource.Error("No user logged in."))
            return@flow
        }

        try {
            val documentSnapshot = firestore.collection("users").document(userId).get().await()
            val role = documentSnapshot.getString("role") ?: "Unknown"
            emit(Resource.Success(role))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred while fetching the user role."))
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
                "${barcode.sms}"
            }

            Barcode.TYPE_TEXT -> {
                "${barcode.rawValue}"
            }

            Barcode.TYPE_UNKNOWN -> {
                "unknown : ${barcode.rawValue}"
            }

            else -> {
                "Couldn't determine"
            }
        }
    }
    override fun fetchUserProfile(userId: String): Flow<Resource<UserProfile>> = flow {
        try {
            emit(Resource.Loading())
            val documentSnapshot = firestore.collection("users").document(userId).get().await()
            val userProfile = documentSnapshot.toObject(UserProfile::class.java)
            userProfile?.let {
                emit(Resource.Success(it))
            } ?: emit(Resource.Error("User profile not found"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error fetching user profile"))
        }
    }

    override fun updateUserProfile(userId: String, userProfile: UserProfile): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            firestore.collection("users").document(userId).set(userProfile).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error updating user profile"))
        }
    }


    override fun checkAndUpdateProductStatus(productbarcodeId: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())


        val querySnapshot = firestore.collection("products")
            .whereEqualTo("productBarcodeID", productbarcodeId)
            .get()
            .await()
        Log.d("ProductStatus", "Loading started for product ID: $productbarcodeId")
        Log.d("ProductStatus", "Query completed. Found ${querySnapshot.size()} documents.")

        // Check if the query returned any documents
        if (querySnapshot.documents.isEmpty()) {
            Log.d("ProductStatus", "No documents found for productBarcodeID: $productbarcodeId")
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
                    val productName = documentSnapshot.getString("productName")
                    Log.d("ProductData", "Product name: $productName")
                    Log.d("ProductStatusDebug", "${it.status},${it.productId},${it.productBarcodeID},${it.productDescription}")

                    emit(Resource.Error("Product has already been borrowed."))
                }
                "reserved" -> {
                    Log.d("ProductStatusDebug", "Product is reserved and cannot be borrowed at this time.")
                    emit(Resource.Error("Product is reserved and cannot be borrowed at this time."))
                }
                "available" -> {
                    Log.d("ProductStatusDebug", "Attempting to update product status to loaned for document ID: ${documentSnapshot.id}")
                    // Update the status to "loaned" if the product is currently available
                    firestore.collection("products").document(documentSnapshot.id)
                        .update("status", "loaned").await()


                    val startDate = Calendar.getInstance().time
                    val calendar = Calendar.getInstance()
                    calendar.time = startDate
                    calendar.add(Calendar.MONTH, 3) // Add 3 months to the start date
                    val endDate = calendar.time

                    var newLoan = Loan(
                        loanId = UUID.randomUUID().toString(),
                        userId = getCurrentUserId(),
                        productId = product.productId,
                        productName = product.productName,
                        productDescription = product.productDescription,
                        productCategory = product.productCategory,
                        productBarcodeID = product.productBarcodeID,
                        startDate = Timestamp(startDate),
                        endDate = Timestamp(endDate),
                        status = "active"
                    )

                    createLoan(newLoan)

                    // Log a success message after the loan is created
                    Log.d("CreateLoan", "Loan created successfully.")
                    // Use the formatDetails method to format the loan details
                    val loanDetails = newLoan.formatDetails()
                    emit(Resource.Success(loanDetails))
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

    override fun <T : Any> writeToFirestore(collectionName: String, dataModel: T, documentId: String?) {
        val firestoreInstance = FirebaseFirestore.getInstance()
        val documentReference = if (documentId != null) {
            firestoreInstance.collection(collectionName).document(documentId)
        } else {
            firestoreInstance.collection(collectionName).document()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                documentReference.set(dataModel).await()
                // Log success message
                Log.d("FirestoreSuccess", "Document successfully written in $collectionName!")
            } catch (e: Exception) {
                // Log error message
                Log.e("FirestoreError", "Error writing document to $collectionName: ${e.message}")
            }
        }
    }
    override fun <T : Any> writeToFirestoreflow(collectionName: String, dataModel: T, documentId: String?): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val documentReference = if (documentId != null) {
                firestoreInstance.collection(collectionName).document(documentId)
            } else {
                firestoreInstance.collection(collectionName).document()
            }
            documentReference.set(dataModel).await()
            emit(Resource.Success(Unit)) // Emit success after the document is successfully written
            Log.d("FirestoreSuccess", "Document successfully written in $collectionName!")
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred while creating the document."))
            Log.e("FirestoreError", "Error writing document to $collectionName: ${e.message}")
        }
    }

    override fun createLoan(loan: Loan) {
        val firestoreInstance = FirebaseFirestore.getInstance()
        val documentReference = firestoreInstance.collection("loans").document()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                documentReference.set(loan).await()
                // Log success message
                Log.d("FirestoreSuccess", "Document successfully written!")
            } catch (e: Exception) {
                // Log error message
                Log.e("FirestoreError", "Error writing document: ${e.message}")
            }
        }
    }

    override fun getUserLoans(userId: String): Flow<Resource<List<Loan>>> = flow {
        try {
            val loansSnapshot = firestore.collection("loans")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val loans = loansSnapshot.toObjects(Loan::class.java)
            emit(Resource.Success(loans))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to fetch loans: ${e.message}"))
        }
    }


    // Function to check resource availability
    override fun checkResourceAvailability(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())

        val snapshot = firestore.collection("products")
            .whereEqualTo("status", "available")
            .get()
            .await()

        val products = snapshot.toObjects(Product::class.java)
        emit(Resource.Success(products))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred while fetching available products."))
    }

    // Function to initiate a new loan
    /*   override fun createLoan(loan: Loan): Flow<Resource<Unit>> = flow {
           emit(Resource.Loading())

           val firestore = firestore.collection("loans").document().set(loan)
           emit(Resource.Success(Unit))
       }.catch { e ->
           emit(Resource.Error(e.message ?: "An error occurred while creating the loan."))
       }
   */

    // Function to report a defective product
    override fun reportDefectiveProduct(defectReport: Report): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        // Check if a report for the given loanId already exists
        val querySnapshot = firestore.collection("reports")
            .whereEqualTo("loanId", defectReport.loanId)
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            // No existing report found for the loanId, proceed with submitting the new report
            firestore.collection("reports").add(defectReport).await()
            emit(Resource.Success(Unit)) // Report submission successful
        } else {
            // Report for the given loanId already exists
            emit(Resource.Error("A report for this loan has already been submitted."))
        }
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred while attempting to report the defect."))
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
    override fun fetchProduct(product:String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        val snapshot = firestore.collection("products")
            .whereEqualTo("productName",product )
            .get()
            .await()

        val productDetails = StringBuilder()
        snapshot.documents.forEach { document ->
            document.data?.forEach { (key, value) ->
                productDetails.append("$key: $value\n")
            }
        }
        emit(Resource.Success(productDetails.toString()))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred while fetching available products."))
    }

    override fun getToken(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("User not logged in")
        try {
            val token = suspendCancellableCoroutine<String> { continuation ->
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(task.result)
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Failed to get FCM token"))
                    }
                }
            }
            // Explicitly specify the map type to match Firestore expectations
            val data: Map<String, Any> = hashMapOf("fcmtoken" to token as Any)
            firestore.collection("users").document(userId).update(data).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error fetching FCM token"))
        }
    }
    override suspend fun createDuplicateLoanWithNewEndDate(loanId: String, currentEndDate: Timestamp) {
        val firestoreInstance = FirebaseFirestore.getInstance()
        val loansCollection = firestoreInstance.collection("loans")
        try {
            // Fetch the original loan document
            val originalLoanDocument = loansCollection.document(loanId).get().await()
            val originalLoan = originalLoanDocument.toObject(Loan::class.java)

            if (originalLoan != null) {
                // Create a new Loan object by copying details from the original loan
                val newLoan = originalLoan.copy(
                    loanId = "", // Reset the loan ID, as it will be generated new upon document creation
                    endDate = Timestamp(Date(currentEndDate.seconds * 1000 + 7889231400L)) // Calculate the new end date
                )

                // Add the new loan to the collection, auto-generating a new document ID
                val newDocumentRef = loansCollection.document()
                newLoan.loanId = newDocumentRef.id // Set the new loan ID to the new document's auto-generated ID

                newDocumentRef.set(newLoan).await()
            }
        } catch (e: Exception) {
            // Handle the error
        }
    }
}