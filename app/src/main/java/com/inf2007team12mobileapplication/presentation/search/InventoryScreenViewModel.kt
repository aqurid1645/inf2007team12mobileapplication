package com.inf2007team12mobileapplication.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inf2007team12mobileapplication.data.Repo
import com.inf2007team12mobileapplication.data.Resource
import com.inf2007team12mobileapplication.data.model.InventoryScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class InventoryScreenViewModel @Inject constructor(
    private val repository: Repo
) : ViewModel() {
    private val _state = MutableStateFlow(InventoryScreenState())
    val state = _state.asStateFlow()

    fun getProduct(product: String) {
        viewModelScope.launch {
            repository.fetchProduct(product).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Update UI to show loading message
                    }

                    is Resource.Success -> {
                        val products = resource.data
                        if (products != null) {
                            if (products.isEmpty()) {
                                _state.value = _state.value.copy(
                                    productDetails = "No item found for: $product")
                            } else {
                                _state.value = _state.value.copy(
                                    productDetails = products
                                )
                            }
                        }
                    }

                    is Resource.Error -> {
                        // Update UI for error
                    }
                }
            }
        }
    }
}
