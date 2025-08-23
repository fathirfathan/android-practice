package com.effatheresoft.androidpractice.data.remote

import com.effatheresoft.androidpractice.data.local.TaskEntity
import com.google.gson.annotations.SerializedName

data class TaskResponse(

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("completed")
    val completed: String
)

fun List<TaskResponse>.toEntity(): List<TaskEntity> {
    val taskEntities = mutableListOf<TaskEntity>()
    for (task in this) {
        val isCompleted = task.completed == "true"
        val entity = TaskEntity(
            task.id,
            task.title,
            isCompleted
        )
        taskEntities.add(entity)
    }
    return taskEntities
}