package com.effatheresoft.androidpractice.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("products")
    fun getProducts(
        @Query("skip") skipPageNumber: Int,
        @Query("limit") pageSize: Int
    ): Call<ProductResponse>

    @GET("products")
    suspend fun getProductsWithCoroutine(
        @Query("skip") skipPageNumber: Int,
        @Query("limit") pageSize: Int
    ): ProductResponse
}
