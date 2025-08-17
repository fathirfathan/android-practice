package com.effatheresoft.androidpractice

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.effatheresoft.androidpractice.databinding.ActivityMainBinding
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
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom)
            insets
        }

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        binding.textViewProgress.visibility = GONE
        binding.buttonSend.setOnClickListener {
            binding.buttonSend.isEnabled = false
            if (!binding.textViewProgress.isVisible) binding.textViewProgress.visibility = VISIBLE

            executor.execute {
                for (i in 1..100) {
                    Thread.sleep(20)
                    val percentage = i * 1
                    handler.post {
                        binding.textViewProgress.text = resources.getString(
                            R.string.progress,
                            percentage)
                    }

                    if (percentage == 100) {
                        handler.post {
                            binding.buttonSend.isEnabled = true
                        }
                    }
                }
            }
        }
    }
}