package com.effatheresoft.androidpractice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class TaskWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    private var resultStatus: Result? = null

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_IS_SUCCESS = "extra_is_success"
        const val NOTIFICATION_CHANNEL_ID_WORKER = "notification_channel_id_worker"
        const val NOTIFICATION_CHANNEL_NAME_WORKER = "notification_channel_name_worker"
        const val NOTIFICATION_ID_WORKER = 5
    }

    override fun doWork(): Result {
        val data = inputData.getString(EXTRA_DATA)
        val isSuccess = inputData.getBoolean(EXTRA_IS_SUCCESS, true)
        return getTask(data, isSuccess)
    }

    fun getTask(data: String?, isSuccess: Boolean): Result {
        Thread.sleep(1_000)
        if(isSuccess) {
            pushNotification("Task is successfully run. Data: $data")
            resultStatus = Result.success()
        } else {
            pushNotification("Task has failed")
            resultStatus = Result.failure()
        }
        return resultStatus as Result
    }

    fun pushNotification(message: String) {
        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat
            .Builder(applicationContext, NOTIFICATION_CHANNEL_ID_WORKER).apply {
                setSmallIcon(R.drawable.outline_alarm_24)
                setContentTitle("Task Worker")
                setContentText(message)
                setPriority(NotificationCompat.PRIORITY_HIGH)
                setDefaults(NotificationCompat.DEFAULT_ALL)
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID_WORKER,
                NOTIFICATION_CHANNEL_NAME_WORKER,
                NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(NOTIFICATION_CHANNEL_ID_WORKER)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID_WORKER, notification.build())
    }
}