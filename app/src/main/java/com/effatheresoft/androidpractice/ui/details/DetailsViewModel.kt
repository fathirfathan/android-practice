package com.effatheresoft.androidpractice.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.effatheresoft.androidpractice.data.TaskRepository
import com.effatheresoft.androidpractice.data.local.TaskEntity
import com.effatheresoft.androidpractice.util.Result

class DetailsViewModel(val repository: TaskRepository): ViewModel() {
    private val currentTask = MediatorLiveData<Result<Task>>(Result.Loading)

    fun getCurrentTask(id: String): LiveData<Result<Task>> {
        currentTask.value = Result.Loading
        val task = repository.getTaskById(id)
        currentTask.addSource(task) { newData ->
            currentTask.value = newData
        }
        return currentTask
    }
}

fun TaskEntity.toTask(): Task {
    return Task(this.id, this.title, "", this.completed)
}

data class Task(
    val id: String,
    val title: String,
    val details: String,
    val isCompleted: Boolean
)