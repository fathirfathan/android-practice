package com.effatheresoft.androidpractice

import android.os.Bundle
import android.os.Handler
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.effatheresoft.androidpractice.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import java.util.concurrent.Executors

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
                binding.recyclerViewTasks.adapter = TaskAdapter(tasks)
                binding.progressBarTasks.visibility = GONE
            }
        }
    }

    fun fetchAllTasks(callback: (ArrayList<String>) -> Unit) {
        val apiEndpoint = "https://68a31757c5a31eb7bb1ee984.mockapi.io/tasks"
        val client = AsyncHttpClient()
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(mainLooper)
        val tasks = ArrayList<String>()

        client.get(apiEndpoint, object: AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                executor.execute {
                    val responseString = String(responseBody)
                    val responseArray = JSONArray(responseString)
                    for (i in 0 until responseArray.length()) {
                        val responseObject = responseArray.getJSONObject(i)
                        val title = responseObject.getString("title")
                        tasks.add(title)
                    }
                    handler.post { callback(tasks) }
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                callback(tasks)
            }

        })
    }
}
