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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.progressBarTasks.visibility = GONE
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.buttonGetTasks.setOnClickListener {
            binding.progressBarTasks.visibility = VISIBLE
            fetchAllTasks { tasks ->
                val taskAdapter = TaskAdapter()
                taskAdapter.submitList(tasks)
                binding.recyclerViewTasks.adapter = taskAdapter
                binding.progressBarTasks.visibility = GONE
            }
        }
    }

    fun fetchAllTasks(callback: (List<TaskResponse>) -> Unit) {
        val baseUrl = "https://68a31757c5a31eb7bb1ee984.mockapi.io/"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(TaskApiService::class.java)

        apiService.fetchAllTasks().enqueue(object: Callback<List<TaskResponse>> {
            override fun onResponse(
                call: Call<List<TaskResponse>>,
                response: Response<List<TaskResponse>>
            ) {
                val taskResponse = response.body()
                if (taskResponse != null) { callback(taskResponse) }
            }

            override fun onFailure(
                call: Call<List<TaskResponse>>,
                t: Throwable
            ) {

            }
        })
    }
}
