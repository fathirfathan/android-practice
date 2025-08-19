package com.effatheresoft.androidpractice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    private var _adapter = MutableLiveData<TasksAdapter>()
    val adapter: LiveData<TasksAdapter> = _adapter

    private var _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var _addTaskEvent = MutableLiveData<Event<String>>()
    val addTaskEvent: LiveData<Event<String>> = _addTaskEvent

    init {
        _adapter.value = TasksAdapter()
    }

    fun getAndSubmitTasksToAdapter() {
        _isLoading.value = true
        val apiCall = ApiConfig.getApiService().getAllTasks()
        apiCall.enqueue(object: Callback<List<TaskResponse>> {
            override fun onResponse(
                call: Call<List<TaskResponse>>,
                response: Response<List<TaskResponse>>
            ) {
                if (response.isSuccessful) {
                    _adapter.value?.submitList(response.body())
                    _isLoading.value = false
                } else _isLoading.value = false
            }

            override fun onFailure(
                call: Call<List<TaskResponse>>,
                t: Throwable
            ) {
                _isLoading.value = false
            }

        })
    }

    fun addTask(task: Task) {
        _isLoading.value = true
        val apiCall = ApiConfig.getApiService().addTask(task)
        apiCall.enqueue(object: Callback<Any> {
            override fun onResponse(
                call: Call<in Any>,
                response: Response<in Any>
            ) {
                _addTaskEvent.value = Event("succeed")
                getAndSubmitTasksToAdapter()
            }

            override fun onFailure(call: Call<in Any>, t: Throwable) {
                _isLoading.value = false
                _addTaskEvent.value = Event("failed")
            }

        })
    }
}
