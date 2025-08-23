package com.effatheresoft.androidpractice.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.effatheresoft.androidpractice.data.local.TaskDao
import com.effatheresoft.androidpractice.data.remote.ApiService
import com.effatheresoft.androidpractice.data.remote.TaskResponse
import com.effatheresoft.androidpractice.data.remote.toEntity
import com.effatheresoft.androidpractice.ui.details.Task
import com.effatheresoft.androidpractice.ui.details.toTask
import com.effatheresoft.androidpractice.util.AppExecutor
import com.effatheresoft.androidpractice.util.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskRepository(
    private val apiService: ApiService,
    private val taskDao: TaskDao,
    val executor: AppExecutor
) {
    private val tasks = MediatorLiveData<Result<List<Task>>>()

    fun getTasks(): LiveData<Result<List<Task>>> {
        tasks.value = Result.Loading
        val taskCall = apiService.getTasks()
        taskCall.enqueue(object: Callback<List<TaskResponse>> {
            override fun onResponse(
                call: Call<List<TaskResponse>?>,
                response: Response<List<TaskResponse>?>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        executor.singleThread.execute {
                            val taskEntity = responseBody.toEntity()
                            taskDao.insertTasks(taskEntity)
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<List<TaskResponse>?>,
                t: Throwable
            ) {
                tasks.value = Result.Error("2")
            }
        })
        tasks.addSource(taskDao.getTasks()) {
            val taskList = mutableListOf<Task>()
            for (task in it) taskList.add(task.toTask())
            tasks.value = Result.Success(taskList)
        }
        return tasks
    }

    fun getTaskById(id: String): LiveData<Result<Task>> {
        return taskDao.getTaskById(id).map { it -> Result.Success(it.toTask()) }
    }

    fun deleteAllTasks() = executor.singleThread.execute{ taskDao.deleteAllTasks() }

    companion object {
        @Volatile
        private var instance: TaskRepository? = null
        fun getInstance(
            apiService: ApiService,
            taskDao: TaskDao
        ) = instance ?: synchronized(this) {
            instance ?: TaskRepository(apiService, taskDao, AppExecutor())
        }.also { instance = it }
    }
}