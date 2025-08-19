package com.effatheresoft.androidpractice

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.effatheresoft.androidpractice.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom)
            insets
        }

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)

        viewModel.adapter.observe(this) { adapter ->
            binding.recyclerViewTasks.adapter = adapter
        }
        viewModel.isLoading.observe(this) { isLoading ->
            when(isLoading) {
                true -> { binding.progressBarTasks.visibility = VISIBLE }
                false -> { binding.progressBarTasks.visibility = GONE }
            }
        }
        viewModel.addTaskEvent.observe(this) { addTaskEvent ->
            addTaskEvent.getEventIfNotHandledYet()?.let {
                val message = when(it) {
                    "succeed" -> "Task Successfully Added"
                    "failed" -> "Failed to Add Task"
                    else -> "Error"
                }
                Snackbar.make(
                    window.decorView.rootView,
                    message,
                    Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        binding.buttonShowTasks.setOnClickListener {
            viewModel.getAndSubmitTasksToAdapter()
        }

        binding.buttonAddTask.setOnClickListener {
            val newTask = Task(
                binding.textInputLayoutTask.editText?.text.toString(),
                false
            )
            viewModel.addTask(newTask)
        }
    }
}

class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getEventIfNotHandledYet(): T? {
        return if (hasBeenHandled) null else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}