package com.inf2007team12mobileapplication.presentation.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ProductCatalogViewModel : ViewModel() {
    private val _products = MutableLiveData<List<ProductCatalog>>()
    val products: LiveData<List<ProductCatalog>> = _products

    init {
        FirebaseFirestore.getInstance().collection("products")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Log the error or handle the exception
                    return@addSnapshotListener
                }
                val productList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<ProductCatalog>()?.apply {
                        // Here you can assign the document ID to a field if needed
                    }
                }.orEmpty()
                _products.value = productList
            }
    }
}
