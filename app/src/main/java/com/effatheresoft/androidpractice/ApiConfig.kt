package com.effatheresoft.androidpractice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(): TaskApiService {
            val baseUrl = "https://68a31757c5a31eb7bb1ee984.mockapi.io/"
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(TaskApiService::class.java)
        }
    }
}
