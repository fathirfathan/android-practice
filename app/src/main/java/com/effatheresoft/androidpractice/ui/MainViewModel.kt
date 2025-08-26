package com.effatheresoft.androidpractice.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.effatheresoft.androidpractice.data.Product
import com.effatheresoft.androidpractice.data.ProductRepository

class MainViewModel(val repository: ProductRepository): ViewModel() {
    val products: LiveData<PagingData<Product>> = repository.getProductsPagingData().cachedIn(viewModelScope)

    fun getProductsFromLocal() = repository.getProductsFromLocal()
    fun getProductsFromRemote() = repository.getProductsFromRemote()
    fun deleteAllProducts() = repository.deleteAllProducts()

}
