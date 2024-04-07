
package com.inf2007team12mobileapplication.data.model

import kotlin.random.Random



data class Product(
     val productId: String = "",
     val productName: String = "",
    val productDescription: String = "",
    val productCategory: String = "",
    val status: String = "",
     val productBarcodeID: String = ""
)

// Counter for product ID
var productIdCounter = 1
fun generateRandomProduct(): Product {
    val categories = listOf("electronics", "household", "toys", "books", "clothing", "food")
    val names = listOf("raspberry pi", "Arduino", "LED TV", "Smartphone", "Laptop", "Speaker")
    val descriptions = listOf("microcontroller", "entertainment device", "portable computer", "audio equipment", "reading material", "apparel")
    val statuses = listOf("available", "loaned", "reserved")

    val product = Product(
        productBarcodeID = Random.nextLong(1000000000000, 9999999999999).toString(),
        productCategory = categories.random(),
        productDescription = descriptions.random(),
        productId = productIdCounter++.toString(),
        productName = names.random(),
        status = statuses.random()
    )

    return product
}

