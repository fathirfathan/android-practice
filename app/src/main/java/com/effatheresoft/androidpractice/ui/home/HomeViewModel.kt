package com.effatheresoft.androidpractice.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.effatheresoft.androidpractice.data.TaskRepository
import com.effatheresoft.androidpractice.data.local.TaskEntity
import com.effatheresoft.androidpractice.ui.details.Task
import com.effatheresoft.androidpractice.util.Result

class HomeViewModel(private val repository: TaskRepository): ViewModel() {
    fun getTasks(): LiveData<Result<List<Task>>> = repository.getTasks()

    fun deleteAllTasks() = repository.deleteAllTasks()
}