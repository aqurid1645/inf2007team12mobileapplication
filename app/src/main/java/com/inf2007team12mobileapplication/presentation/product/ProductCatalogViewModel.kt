package com.inf2007team12mobileapplication.presentation.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inf2007team12mobileapplication.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.inf2007team12mobileapplication.data.RepoImpt
@HiltViewModel
class ProductCatalogViewModel @Inject constructor(
    private val repo: RepoImpt
) : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    init {
        viewModelScope.launch {
            repo.fetchProducts().collect { productList ->
                _products.value = productList
            }
        }
    }


}
