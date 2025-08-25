package com.effatheresoft.androidpractice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.effatheresoft.androidpractice.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBroadcastReceiver: BroadcastReceiver
    private lateinit var timeStringFormat: String
    private lateinit var broadcastDataStringFormat: String

    companion object {
        const val ACTION_LONG_RUNNING = "long_running_action"
    }

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

        timeStringFormat = getString(R.string.elapsed_time)
        broadcastDataStringFormat = getString(R.string.received_broadcast_data)

        val actionIntentFilter = IntentFilter(ACTION_LONG_RUNNING)
        actionBroadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                binding.textViewBroadcastData.text =
                    broadcastDataStringFormat.format("Received")
            }
        }
        ContextCompat.registerReceiver(
            this,
            actionBroadcastReceiver,
            actionIntentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED)

        binding.textViewBroadcastData.text = broadcastDataStringFormat.format("Not Received")
        binding.textViewElapsedTime.text = timeStringFormat.format("0.00s")

        binding.buttonSendBroadcast.setOnClickListener(this)
        binding.buttonResetBroadcast.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        val timeStringFormat = getString(R.string.elapsed_time)
        val broadcastDataFormat = getString(R.string.received_broadcast_data)

        when(view) {
            binding.buttonSendBroadcast -> {
                executor.execute {
                    for (i in 1..100) {
                        Thread.sleep(10)
                        val timeString = run{
                            if (i in 10..99)
                                return@run timeStringFormat.format("0.${i}s")
                            if (i in 1..9)
                                return@run timeStringFormat.format("0.0${i}s")
                            return@run timeStringFormat.format("1.00s")
                        }
                        handler.post {
                            binding.textViewElapsedTime.text = timeString
                        }
                    }
                }
                handler.postDelayed({
                    val notifyActionFinishedIntent = Intent().setAction(ACTION_LONG_RUNNING)
                        .setPackage(packageName)
                    sendBroadcast(notifyActionFinishedIntent)
                }, 1_000)
            }
            binding.buttonResetBroadcast -> {
                binding.textViewElapsedTime.text = timeStringFormat.format("0.00s")
                binding.textViewBroadcastData.text = broadcastDataFormat.format("Not Received")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(actionBroadcastReceiver)
    }
}