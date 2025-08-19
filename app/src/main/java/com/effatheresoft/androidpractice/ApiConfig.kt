package com.effatheresoft.androidpractice

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://68a31757c5a31eb7bb1ee984.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}

interface ApiService {
    @GET("tasks")
    fun getAllTasks(): Call<List<TaskResponse>>

    @POST("tasks")
    fun addTask(@Body task: Task): Call<Any>
}
