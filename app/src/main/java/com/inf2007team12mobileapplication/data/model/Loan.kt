package com.inf2007team12mobileapplication.data.model

import com.google.firebase.Timestamp

import java.text.SimpleDateFormat
import java.util.Locale

data class Loan(
    var loanId: String = "",
    val userId: String = "",
    val productId: String = "",
    val productCategory: String = "",
    val productDescription: String = "",
    val productBarcodeID: String = "",
    val productName: String = "",
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val status: String = ""
) {
    fun formatDetails(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return """
            Loan ID: $loanId
            Product Name: $productName
            Product ID: $productId
            Product Category: $productCategory
            Product Description: $productDescription
            Start Date: ${startDate?.let { sdf.format(it.toDate()) } ?: "N/A"}
            End Date: ${endDate?.let { sdf.format(it.toDate()) } ?: "N/A"}
            Status: $status
        """.trimIndent()
    }
}
