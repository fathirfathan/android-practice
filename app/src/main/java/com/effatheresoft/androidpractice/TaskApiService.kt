package com.effatheresoft.androidpractice

import retrofit2.Call
import retrofit2.http.GET

interface TaskApiService {
    @GET("tasks")
    fun fetchAllTasks(): Call<List<TaskResponse>>
}
