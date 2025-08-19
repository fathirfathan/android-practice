package com.effatheresoft.androidpractice

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.effatheresoft.androidpractice.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showLoading(false)
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.buttonGetTasks.setOnClickListener {
            showLoading(true)
            fetchAllTasks { tasks ->
                attachAdapterWithTasks(tasks)
                showLoading(false)
            }
        }
        binding.buttonAddTask.setOnClickListener {
            showLoading(true)
            val newTask = TaskRequest(
                binding.textInputLayoutTask.editText?.text.toString(), false)
            addTask(newTask) {
                fetchAllTasks { tasks ->
                    attachAdapterWithTasks(tasks)
                    showLoading(false)
                    binding.textInputLayoutTask.editText?.setText("")
                }
            }
        }
    }

    fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarTasks.visibility = VISIBLE
        } else {
            binding.progressBarTasks.visibility = GONE
        }
    }

    fun attachAdapterWithTasks(tasks: List<TaskResponse>) {
        val taskAdapter = TaskAdapter()
        taskAdapter.submitList(tasks)
        binding.recyclerViewTasks.adapter = taskAdapter
    }

    fun fetchAllTasks(callback: (List<TaskResponse>) -> Unit) {
        ApiConfig.getApiService().fetchAllTasks().enqueue(object: Callback<List<TaskResponse>> {
            override fun onResponse(
                call: Call<List<TaskResponse>>,
                response: Response<List<TaskResponse>>
            ) {
                val taskResponse = response.body()
                if (taskResponse != null) callback(taskResponse)
            }

            override fun onFailure(
                call: Call<List<TaskResponse>>,
                t: Throwable
            ) {

            }
        })
    }

    fun addTask(task: TaskRequest, callback: () -> Unit) {
        ApiConfig.getApiService().createTask(task).enqueue(object: Callback<Any> {
            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful) callback()
            }

            override fun onFailure(
                call: Call<Any>,
                t: Throwable
            ) {

            }
        })
    }
}
