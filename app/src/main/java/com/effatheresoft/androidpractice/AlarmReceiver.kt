package com.effatheresoft.androidpractice

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ALARM_ID = 3
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_TYPE = "extra_type"
        const val NOTIFICATION_CHANNEL_ID_ALARM = "notification_channel_id_alarm"
        const val NOTIFICATION_CHANNEL_NAME_ALARM = "notification_channel_name_alarm"
        const val NOTIFICATION_ID_ALARM = 2
        const val TYPE_DAILY = "type_daily"
        const val TYPE_ONCE = "type_once"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE)
        val data = intent.getStringExtra(EXTRA_DATA) ?: "Default Text"
        val title = when(type) {
            TYPE_ONCE -> "One Time Notification"
            TYPE_DAILY -> "Daily Notification"
            else -> "Default Text" }

        pushNotification(context, title, data)
    }

    fun setOneTimeAlarm(context: Context, timeInMillis: Long, message: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_TYPE, TYPE_ONCE)
            putExtra(EXTRA_DATA, message) }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent)
    }

    fun pushNotification(context: Context, title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID_ALARM)
            .setSmallIcon(R.drawable.outline_alarm_24)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(notificationSound)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID_ALARM,
                NOTIFICATION_CHANNEL_NAME_ALARM,
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            }
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID_ALARM)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_ID_ALARM, notification)
    }
}

