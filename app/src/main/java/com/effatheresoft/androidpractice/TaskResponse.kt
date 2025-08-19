package com.effatheresoft.androidpractice

import com.google.gson.annotations.SerializedName

data class TaskResponse(

	@field:SerializedName("completed")
	val completed: Boolean,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("title")
	val title: String
)

data class Task(

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("completed")
	val completed: Boolean
)
