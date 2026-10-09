package com.fitlife.app.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.fitlife.app.FitLifeApp
import com.fitlife.app.R
import com.fitlife.app.data.AppDb
import com.fitlife.app.data.UserStore
import com.fitlife.app.ui.MainActivity
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class WaterReminderWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val hour = LocalTime.now().hour
        if (hour < 7 || hour >= 22) return Result.success()   // quiet hours

        val ctx = applicationContext
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val goal = Calc.waterMl(UserStore(ctx).weightKg)
        val drunk = AppDb.get(ctx).waterDao().totalOnce(LocalDate.now().toEpochDay())
        if (drunk >= goal) return Result.success()

        val pi = PendingIntent.getActivity(
            ctx, 0, Intent(ctx, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val n = NotificationCompat.Builder(ctx, FitLifeApp.CHANNEL_WATER)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("💧 Time for some water")
            .setContentText("You've had %.1f L today. Goal: %.1f L.".format(drunk / 1000.0, goal / 1000.0))
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(ctx).notify(1001, n)
        return Result.success()
    }
}

object ReminderScheduler {
    private const val NAME = "water_reminder"

    fun apply(ctx: Context, hours: Int) {
        val wm = WorkManager.getInstance(ctx)
        if (hours <= 0) {
            wm.cancelUniqueWork(NAME)
            return
        }
        val req = PeriodicWorkRequestBuilder<WaterReminderWorker>(hours.toLong(), TimeUnit.HOURS).build()
        wm.enqueueUniquePeriodicWork(NAME, ExistingPeriodicWorkPolicy.UPDATE, req)
    }
}
