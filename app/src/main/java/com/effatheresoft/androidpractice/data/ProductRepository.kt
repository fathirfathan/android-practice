package com.effatheresoft.androidpractice.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.effatheresoft.androidpractice.data.local.ProductDatabase
import com.effatheresoft.androidpractice.data.local.toProductList
import com.effatheresoft.androidpractice.data.paging.ProductPagingSource
import com.effatheresoft.androidpractice.data.remote.ApiService
import com.effatheresoft.androidpractice.data.remote.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class ProductRepository(val database: ProductDatabase, val apiService: ApiService) {
    val products = MediatorLiveData<List<Product>>()

    fun getProductsFromRemote(): LiveData<List<Product>> {
        val apiCall = apiService.getProducts(0, 10)
        apiCall.enqueue(object: Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse?>,
                response: Response<ProductResponse?>
            ) {
                if (response.isSuccessful) {
                    val productList = mutableListOf<Product>()
                    val responseProducts = response.body()!!.products
                    for(product in responseProducts) {
                        productList.add(Product(
                            product.id,
                            product.title,
                            product.description
                        ))
                    }
                    Executors.newSingleThreadExecutor().execute {
                        database.productDao().insertProducts(productList.toProductEntityList())
                    }
                    products.value = productList
                }
            }

            override fun onFailure(
                call: Call<ProductResponse?>,
                t: Throwable
            ) {
                print("error")
                print("error")
            }

        })
        return products
    }

    fun getProductsFromLocal(): LiveData<List<Product>> {
        val databaseProducts = database.productDao().getProducts().map { it -> it.toProductList() }
        products.addSource(databaseProducts) { products.value = it }
        return products
    }

    fun getProductsPagingData(): LiveData<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                ProductPagingSource(apiService)
            }
        ).liveData
    }

    fun deleteAllProducts() {
        Executors.newSingleThreadExecutor().execute {
            database.productDao().deleteAllProducts()
        }
    }
}
