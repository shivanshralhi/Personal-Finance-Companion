package com.plcoding.personalfinancecompanion.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object ReminderNotificationHelper {
    const val CHANNEL_ID = "daily_finance_reminder"
    const val CHANNEL_NAME = "Finance reminders"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily reminders to log transactions and track goals"
        }
        manager.createNotificationChannel(channel)
    }
}