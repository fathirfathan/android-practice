package com.effatheresoft.androidpractice

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TaskApiService {
    @GET("tasks")
    fun fetchAllTasks(): Call<List<TaskResponse>>


    @POST("tasks")
    fun createTask(@Body task: TaskRequest): Call<Any>
}
