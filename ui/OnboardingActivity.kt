package com.fitlife.app.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fitlife.app.R
import com.fitlife.app.data.UserStore
import com.fitlife.app.databinding.ActivityOnboardingBinding
import com.fitlife.app.util.ReminderScheduler

class OnboardingActivity : AppCompatActivity() {

    private lateinit var b: ActivityOnboardingBinding
    private lateinit var store: UserStore

    private val askActivity = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        toast(if (it) "Activity access allowed" else "Skipped")
    }
    private val askNotif = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        toast(if (it) "Reminders enabled" else "Skipped")
    }
    private val askLocation = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        toast(if (it.values.any { g -> g }) "Location allowed" else "Skipped")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(b.root)
        store = UserStore(this)

        b.btnPermActivity.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 29) askActivity.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            else toast("Not needed on this Android version")
        }
        b.btnPermNotification.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 33) askNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
            else toast("Notifications already enabled")
        }
        b.btnPermLocation.setOnClickListener {
            askLocation.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
        b.btnGetStarted.setOnClickListener { save() }
    }

    private fun save() {
        val name = b.etName.text.toString().trim()
        val age = b.etAge.text.toString().toIntOrNull()
        val height = b.etHeight.text.toString().toFloatOrNull()
        val weight = b.etWeight.text.toString().toFloatOrNull()

        if (name.isEmpty() || age == null || height == null || weight == null ||
            height < 50 || height > 250 || weight < 10 || weight > 300 || age < 5 || age > 110
        ) {
            toast("Please enter valid name, age, height and weight")
            return
        }
        store.name = name
        store.age = age
        store.heightCm = height
        store.weightKg = weight
        store.goal = when (b.rgGoal.checkedRadioButtonId) {
            R.id.rbLose -> "Lose weight"
            R.id.rbGain -> "Gain weight"
            R.id.rbFit -> "Improve fitness"
            else -> "Maintain weight"
        }
        store.trustedName = b.etTrustedName.text.toString().trim()
        store.trustedPhone = b.etTrustedPhone.text.toString().trim()
        store.onboarded = true

        ReminderScheduler.apply(this, store.reminderHours)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}
