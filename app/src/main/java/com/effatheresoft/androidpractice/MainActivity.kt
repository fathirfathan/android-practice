package com.effatheresoft.androidpractice

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.effatheresoft.androidpractice.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var actionBroadcastReceiver: BroadcastReceiver
    private lateinit var binding: ActivityMainBinding
    private lateinit var broadcastDataStringFormat: String
    private lateinit var timeStringFormat: String

    companion object {
        const val ACTION_LONG_RUNNING = "long_running_action"
        const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
        const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
        const val NOTIFICATION_ID = 1
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

        broadcastDataStringFormat = getString(R.string.received_broadcast_data)
        timeStringFormat = getString(R.string.elapsed_time)

        binding.textViewBroadcastData.text = broadcastDataStringFormat.format("Not Received")
        binding.textViewElapsedTime.text = timeStringFormat.format("0.00s")

        binding.buttonPermission.setOnClickListener(this)
        binding.buttonPushNotification.setOnClickListener(this)
        binding.buttonResetBroadcast.setOnClickListener(this)
        binding.buttonSendBroadcast.setOnClickListener(this)

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
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(actionBroadcastReceiver)
    }

    override fun onClick(view: View) {
        when(view) {
            binding.buttonPermission -> getNotificationPermission()
            binding.buttonPushNotification -> sendNotification()
            binding.buttonResetBroadcast -> resetBroadcastTexts()
            binding.buttonSendBroadcast -> sendBroadcast()
        }
    }

    private fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33)
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        else showToast("Permission is not needed")
    }

    private fun resetBroadcastTexts() {
        binding.textViewBroadcastData.text = broadcastDataStringFormat.format("Not Received")
        binding.textViewElapsedTime.text = timeStringFormat.format("0.00s")
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermissionGranted ->
        if (isPermissionGranted) showToast("Permission is granted")
        else showToast("Permission is rejected")
    }

    private fun sendBroadcast() {
        val singleThreadExecutor = Executors.newSingleThreadExecutor()
        val mainThreadHandler = Handler(Looper.getMainLooper())

        singleThreadExecutor.execute {
            for (i in 1..100) {
                Thread.sleep(10)
                val timeString = run{
                    if (i in 10..99)
                        return@run timeStringFormat.format("0.${i}s")
                    if (i in 1..9)
                        return@run timeStringFormat.format("0.0${i}s")
                    return@run timeStringFormat.format("1.00s")
                }
                mainThreadHandler.post {
                    binding.textViewElapsedTime.text = timeString
                }
            }
        }
        mainThreadHandler.postDelayed({
            val notifyActionFinishedIntent =
                Intent().setAction(ACTION_LONG_RUNNING).setPackage(packageName)
            sendBroadcast(notifyActionFinishedIntent)
        }, 1_000)
    }

    private fun sendNotification() {
        val intent =
            Intent(Intent.ACTION_VIEW, "https://developer.android.com/".toUri())
        val pendingIntentFlag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, pendingIntentFlag)

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setContentText("Click here to go to android docs")
            .setContentTitle("Android Documentation")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.outline_alarm_24)
            .setSubText("Docs")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun showToast(text: String, context: Context = this, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, text, duration).show()
    }
}