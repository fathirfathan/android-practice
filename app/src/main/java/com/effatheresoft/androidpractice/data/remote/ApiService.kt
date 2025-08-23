package com.effatheresoft.androidpractice.data.remote

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("tasks")
    fun getTasks(): Call<List<TaskResponse>>
}

class ApiConfig {
    companion object {
        @Volatile
        private var INSTANCE: ApiService? = null

        fun provideApiService(): ApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://68a31757c5a31eb7bb1ee984.mockapi.io/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    retrofit.create(ApiService::class.java)
                }
            }.also { INSTANCE = it }
        }
    }
}