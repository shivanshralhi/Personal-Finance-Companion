package com.plcoding.personalfinancecompanion.reminders

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_NAME = "daily_finance_reminder_work"

    fun scheduleDailyReminder(context: Context, reminderTime: LocalTime = LocalTime.of(20, 0)) {
        val now = LocalDateTime.now()
        val nextRun = now.withHour(reminderTime.hour)
            .withMinute(reminderTime.minute)
            .withSecond(0)
            .withNano(0)
            .let { if (it.isAfter(now)) it else it.plusDays(1) }

        val initialDelay = Duration.between(now, nextRun).toMinutes().coerceAtLeast(1)

        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
