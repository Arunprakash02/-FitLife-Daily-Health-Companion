package com.fitlife.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fitlife.app.R
import com.fitlife.app.data.AppDb
import com.fitlife.app.data.UserStore
import com.fitlife.app.data.WaterEntity
import com.fitlife.app.databinding.ActivityWaterBinding
import com.fitlife.app.util.Calc
import com.fitlife.app.util.ReminderScheduler
import kotlinx.coroutines.launch
import java.time.LocalDate

class WaterActivity : AppCompatActivity() {

    private lateinit var b: ActivityWaterBinding
    private lateinit var store: UserStore
    private val day get() = LocalDate.now().toEpochDay()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityWaterBinding.inflate(layoutInflater)
        setContentView(b.root)
        store = UserStore(this)
        val dao = AppDb.get(this).waterDao()
        val goal = Calc.waterMl(store.weightKg)

        b.tvWaterInfo.text =
            "Goal for ${store.weightKg.toInt()} kg: %.1f L/day (weight ÷ 20)".format(goal / 1000.0)
        b.tvSchedule.text =
            "= ${Calc.glasses(goal)} glasses of 250 ml. Spread over 7 AM – 10 PM, " +
                    "that's about one glass every ${Calc.smartIntervalMinutes(goal)} min."

        b.btnAdd250.setOnClickListener { add(250) }
        b.btnAdd500.setOnClickListener { add(500) }
        b.btnUndo.setOnClickListener { lifecycleScope.launch { dao.deleteLast(day) } }

        b.rgReminder.check(
            when (store.reminderHours) {
                0 -> R.id.rbOff; 1 -> R.id.rbEvery1; else -> R.id.rbEvery2
            }
        )
        b.rgReminder.setOnCheckedChangeListener { _, id ->
            store.reminderHours = when (id) {
                R.id.rbOff -> 0; R.id.rbEvery1 -> 1; else -> 2
            }
            ReminderScheduler.apply(this, store.reminderHours)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dao.observeTotal(day).collect { ml ->
                    b.tvWaterBig.text = "%.2f / %.1f L".format(ml / 1000.0, goal / 1000.0)
                    b.progressWaterBig.progress = ((ml * 100) / goal).coerceAtMost(100)
                }
            }
        }
    }

    private fun add(ml: Int) {
        lifecycleScope.launch {
            AppDb.get(this@WaterActivity).waterDao()
                .insert(WaterEntity(day = day, time = System.currentTimeMillis(), amountMl = ml))
        }
    }
}
