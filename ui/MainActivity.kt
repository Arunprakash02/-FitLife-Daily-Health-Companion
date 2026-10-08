package com.fitlife.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fitlife.app.R
import com.fitlife.app.data.ActivitySummary
import com.fitlife.app.data.AppDb
import com.fitlife.app.data.HealthConnectManager
import com.fitlife.app.data.UserStore
import com.fitlife.app.databinding.ActivityMainBinding
import com.fitlife.app.util.Calc
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var store: UserStore
    private lateinit var hc: HealthConnectManager

    private val hcPermissions = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { loadHealth() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = UserStore(this)
        if (!store.onboarded) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        hc = HealthConnectManager(this)

        b.ivProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        b.cardWater.setOnClickListener { startActivity(Intent(this, WaterActivity::class.java)) }
        b.cardFood.setOnClickListener { startActivity(Intent(this, FoodActivity::class.java)) }
        b.btnConnectHealth.setOnClickListener { connectHealth() }

        // live water total
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppDb.get(this@MainActivity).waterDao()
                    .observeTotal(LocalDate.now().toEpochDay())
                    .collect { showWater(it) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!::b.isInitialized) return
        val hour = LocalTime.now().hour
        b.tvGreeting.text = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        b.tvUserName.text = "${store.name} 👋"
        b.tvStepGoal.text = "steps  •  Goal %,d".format(store.stepGoal)
        showSummary(ActivitySummary())
        loadHealth()
    }

    private fun connectHealth() {
        if (!hc.isAvailable()) {
            // Android 13 and below: Health Connect is a separate app
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.google.android.apps.healthdata")
                )
            )
            return
        }
        hcPermissions.launch(hc.permissions)
    }

    private fun loadHealth() {
        if (!hc.isAvailable()) {
            b.btnConnectHealth.visibility = View.VISIBLE
            b.btnConnectHealth.text = "Install Health Connect"
            return
        }
        lifecycleScope.launch {
            val granted = runCatching { hc.hasAllPermissions() }.getOrDefault(false)
            if (!granted) {
                b.btnConnectHealth.visibility = View.VISIBLE
                b.btnConnectHealth.text = "❤️ Connect Health Data"
                return@launch
            }
            b.btnConnectHealth.visibility = View.GONE
            runCatching { hc.readToday() }.onSuccess { showSummary(it) }
            runCatching { hc.hourlySteps() }.onSuccess { showHourly(it) }
        }
    }

    private fun showSummary(s: ActivitySummary) {
        b.tvSteps.text = "%,d".format(s.steps)
        b.progressSteps.progress = ((s.steps * 100) / max(1, store.stepGoal)).toInt().coerceAtMost(100)
        b.tvDistance.text = "%.2f km".format(s.distanceKm)
        b.tvCalories.text = "%d kcal".format(s.calories.toInt())
        b.tvWalkTime.text = Calc.formatMinutes(s.walkMinutes)
        b.tvHeartRate.text = s.heartAvg?.let { "$it BPM" } ?: "-- BPM"
    }

    private fun showWater(ml: Int) {
        val goal = Calc.waterMl(store.weightKg)
        b.tvWater.text = "%.1f / %.1f L".format(ml / 1000.0, goal / 1000.0)
        b.progressWater.progress = ((ml * 100) / goal).coerceAtMost(100)
        b.tvNextReminder.text = when (store.reminderHours) {
            0 -> "Reminders off • tap to manage"
            1 -> "⏰ Reminder every 1 hour • tap to log water"
            else -> "⏰ Reminder every ${store.reminderHours} hours • tap to log water"
        }
    }

    private fun showHourly(data: LongArray) {
        val chart = b.hourlyChart
        chart.removeAllViews()
        val maxV = max(1L, data.max())
        val density = resources.displayMetrics.density
        val maxH = (100 * density).toInt()
        for (h in 0 until 24) {
            val bar = View(this).apply {
                setBackgroundResource(R.drawable.bar_bg)
                alpha = if (data[h] == 0L) 0.2f else 1f
            }
            val barH = max((3 * density).toInt(), ((data[h] * maxH) / maxV).toInt())
            val lp = LinearLayout.LayoutParams(0, barH, 1f)
            lp.setMargins(1, 0, 1, 0)
            chart.addView(bar, lp)
        }
        if (data.any { it > 0 }) {
            val best = data.indices.maxByOrNull { data[it] } ?: 0
            b.tvBestHour.text = "Most active hour: ${hourLabel(best)} (${data[best]} steps)"
        } else {
            b.tvBestHour.text = "Most active hour: --"
        }
    }

    private fun hourLabel(h: Int): String {
        val suffix = if (h < 12) "AM" else "PM"
        val hh = if (h % 12 == 0) 12 else h % 12
        return "$hh $suffix"
    }
}
