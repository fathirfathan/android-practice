package com.effatheresoft.androidpractice

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
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
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.effatheresoft.androidpractice.AlarmReceiver.Companion.TYPE_DAILY
import com.effatheresoft.androidpractice.DetailsActivity.Companion.EXTRA_INFO
import com.effatheresoft.androidpractice.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(),
    View.OnClickListener,
    DatePickerFragment.DatePickerListener,
    TimePickerFragment.TimePickerListener
{
    private lateinit var actionBroadcastReceiver: BroadcastReceiver
    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var binding: ActivityMainBinding
    private lateinit var broadcastDataStringFormat: String
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private lateinit var timeStringFormat: String
    private lateinit var workManager: WorkManager

    companion object {
        const val ACTION_LONG_RUNNING = "long_running_action"
        const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
        const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSystemBarsPadding()

        alarmReceiver = AlarmReceiver()
        broadcastDataStringFormat = getString(R.string.received_broadcast_data)
        timeStringFormat = getString(R.string.elapsed_time)
        workManager = WorkManager.getInstance(this)

        binding.textViewBroadcastData.text = broadcastDataStringFormat.format("Not Received")
        binding.textViewElapsedTime.text = timeStringFormat.format("0.00s")

        setOnClickListeners()
        registerActionLongRunningBroadcastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(actionBroadcastReceiver)
    }

    override fun onClick(view: View) {
        when(view) {
            binding.buttonCancelDailyAlarm -> cancelDailyAlarm()
            binding.buttonCancelPeriodicTask -> cancelPeriodicTask()
            binding.buttonPermission -> getNotificationPermission()
            binding.buttonPushNotification -> sendNotificationDocumentation()
            binding.buttonPushNotificationDetails -> sendNotificationDetails()
            binding.buttonResetBroadcast -> resetBroadcastTexts()
            binding.buttonRunOneTimeTask -> runOneTimeTask()
            binding.buttonRunPeriodicTask -> runPeriodicTask()
            binding.buttonSendBroadcast -> sendBroadcast()
            binding.buttonSetAlarm -> setAlarm()
            binding.buttonSetDate -> setDate()
            binding.buttonSetTime -> setTime()
            binding.checkboxIsAlarmDaily -> toggleDatePickerVisibility()
        }
    }

    override fun onDatePicked(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.textViewDate.text = dateFormat.format(calendar.time)
    }

    override fun onTimePicked(hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.textViewTime.text = timeFormat.format(calendar.time)
    }

    fun cancelDailyAlarm() = alarmReceiver.cancelAlarm(this, TYPE_DAILY)

    private fun cancelPeriodicTask() {
        workManager.cancelWorkById(periodicWorkRequest.id)
        binding.buttonRunPeriodicTask.isEnabled = true
    }


    private fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33)
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        else showToast("Permission is not needed")
    }

    private fun isDateInvalid(date: String, format: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            dateFormat.isLenient = false
            dateFormat.parse(date)
            false
        } catch (e: Exception) { true }
    }

    private fun registerActionLongRunningBroadcastReceiver() {
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

    private fun runOneTimeTask() {
        binding.textViewOneTimeTaskStatus.text = "Status:"
        val data = Data.Builder()
            .putString(TaskWorker.EXTRA_DATA, "Some arbitrary data")
            .putBoolean(TaskWorker.EXTRA_IS_SUCCESS, true)
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(TaskWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(oneTimeWorkRequest)
        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.id).observe(this) {
            val resultStatus = it?.state?.name
            binding.textViewOneTimeTaskStatus.append("\n$resultStatus")
        }
    }

    private fun runPeriodicTask() {
        binding.textViewPeriodicTaskStatus.text = "Status:"
        binding.buttonRunPeriodicTask.isEnabled = false
        val data = Data.Builder()
            .putString(TaskWorker.EXTRA_DATA, "Some arbitrary data")
            .putBoolean(TaskWorker.EXTRA_IS_SUCCESS, true)
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        periodicWorkRequest = PeriodicWorkRequest.Builder(TaskWorker::class.java, 15, TimeUnit.MINUTES)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(periodicWorkRequest)
        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this) {
            val resultStatus = it?.state?.name
            binding.textViewPeriodicTaskStatus.append("\n$resultStatus")
            binding.buttonCancelPeriodicTask.isEnabled = false
            if (it?.state == WorkInfo.State.ENQUEUED) {
                binding.buttonCancelPeriodicTask.isEnabled = true
            }
        }
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

    private fun sendNotification(pendingIntent: PendingIntent, texts: NotificationTexts) {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setContentText(texts.text)
            .setContentTitle(texts.title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.outline_alarm_24)
            .setSubText(texts.subtext)
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

    private fun sendNotificationDetails() {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(EXTRA_INFO, "Press back button to navigate back to home")
        }
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) getPendingIntent(
                NOTIFICATION_ID,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            else getPendingIntent(
                NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationTexts = NotificationTexts(
            "Go To Details",
            "Click this to go to details",
            "Details"
        )
        sendNotification(pendingIntent, notificationTexts)
    }

    private fun sendNotificationDocumentation() {
        val intent =
            Intent(Intent.ACTION_VIEW, "https://developer.android.com/".toUri())
        val pendingIntentFlag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, pendingIntentFlag)

        val notificationTexts = NotificationTexts(
            "Android Documentation",
            "Click here to go to android docs",
            "Docs"
        )
        sendNotification(pendingIntent, notificationTexts)
    }

    private fun setAlarm() {
        val date = binding.textViewDate.text.toString()
        val time = binding.textViewTime.text.toString()

        if (isDateInvalid(time, "HH:mm")) {
            showToast("Pick time first", this, Toast.LENGTH_SHORT)
            return }

        if (binding.checkboxIsAlarmDaily.isChecked) {
            setAlarmDaily()
        } else {
            if (isDateInvalid(date, "yyyy-MM-dd")) {
                showToast("Pick date first", this, Toast.LENGTH_SHORT)
                return }
            setAlarmOnce()
        }
    }

    private fun setAlarmDaily() {
        val time = binding.textViewTime.text.toString()
        val timeArray = time.split(":").toTypedArray()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
            set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
            set(Calendar.SECOND, 0) }
        val timeInMillis = calendar.timeInMillis

        alarmReceiver.setDailyAlarm(this, timeInMillis,
            "This notification is created by AlarmReceiver")
    }

    private fun setAlarmOnce() {
        val date = binding.textViewDate.text.toString()
        val time = binding.textViewTime.text.toString()
        val dateArray = date.split("-").toTypedArray()
        val timeArray = time.split(":").toTypedArray()
        val calendar = Calendar.getInstance().apply { set(
            Integer.parseInt(dateArray[0]),
            Integer.parseInt(dateArray[1]) - 1,
            Integer.parseInt(dateArray[2]),
            Integer.parseInt(timeArray[0]),
            Integer.parseInt(timeArray[1]),
            0) }
        val timeInMillis = calendar.timeInMillis

        alarmReceiver.setOneTimeAlarm(this, timeInMillis,
            "This notification is created by AlarmReceiver")
    }

    private fun setDate() = DatePickerFragment().show(supportFragmentManager, null)

    private fun setOnClickListeners() {
        binding.buttonCancelDailyAlarm.setOnClickListener(this)
        binding.buttonCancelPeriodicTask.setOnClickListener(this)
        binding.buttonPermission.setOnClickListener(this)
        binding.buttonPushNotification.setOnClickListener(this)
        binding.buttonPushNotificationDetails.setOnClickListener(this)
        binding.buttonResetBroadcast.setOnClickListener(this)
        binding.buttonRunOneTimeTask.setOnClickListener(this)
        binding.buttonRunPeriodicTask.setOnClickListener(this)
        binding.buttonSendBroadcast.setOnClickListener(this)
        binding.buttonSetAlarm.setOnClickListener(this)
        binding.buttonSetDate.setOnClickListener(this)
        binding.buttonSetTime.setOnClickListener(this)
        binding.checkboxIsAlarmDaily.setOnClickListener(this)
    }

    private fun setSystemBarsPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom)
            insets
        }
    }

    private fun setTime() = TimePickerFragment().show(supportFragmentManager, null)

    private fun showToast(text: String, context: Context = this, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, text, duration).show()
    }

    private fun toggleDatePickerVisibility() {
        if (binding.checkboxIsAlarmDaily.isChecked) {
            binding.buttonSetDate.visibility = View.GONE
            binding.textViewDate.visibility = View.GONE
        }
        else {
            binding.buttonSetDate.visibility = View.VISIBLE
            binding.textViewDate.visibility = View.VISIBLE
        }
    }

    data class NotificationTexts(
        val title: String,
        val text: String,
        val subtext: String
    )
}










private class NotificationExperiment() {
    private data class NotificationChannelInfo(
        val channelId: String,
        val channelName: String
    )
    private data class NotificationInfo(
        val id: Int,
        val title: String,
        val bodyText: String,
        val subtext: String? = null,
        val importance: Int? = null,
    )
    private fun pushNotification(
        context: Context,
        notificationInfo: NotificationInfo,
        notificationChannelInfo: NotificationChannelInfo,
        pendingIntent: PendingIntent? = null
    ) {
        val id = notificationInfo.id
        val title = notificationInfo.title
        val bodyText = notificationInfo.bodyText
        val subtext = notificationInfo.subtext
        val channelId = notificationChannelInfo.channelId
        val channelName = notificationChannelInfo.channelName
        val vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat
            .Builder(context, channelId).apply {
                setColor(ContextCompat.getColor(context, android.R.color.transparent))
                setContentTitle(title)
                setContentText(bodyText)
                setSmallIcon(R.drawable.outline_alarm_24)
                setSound(notificationSound)
                setVibrate(vibrationPattern)

                subtext?.run { setSubText(this) }
                pendingIntent?.run { setContentIntent(pendingIntent).setAutoCancel(true) }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = notificationInfo.importance ?: NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
                enableVibration(true)
                this.vibrationPattern = vibrationPattern
            }
            notificationBuilder.setChannelId(channelId)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = notificationBuilder.build()
        notificationManager.notify(id, notification)
    }
}