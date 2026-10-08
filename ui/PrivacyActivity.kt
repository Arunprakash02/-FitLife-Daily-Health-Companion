package com.fitlife.app.ui

import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Shown by Health Connect when the user taps "privacy policy" in the permission dialog. */
class PrivacyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this).apply {
            setPadding(48, 48, 48, 48)
            textSize = 15f
            text = "FitLife Privacy\n\n" +
                "FitLife reads your steps, distance, active calories and heart rate from Health Connect " +
                "only to show your daily progress. Your data stays on your device. " +
                "You can remove access at any time in Health Connect settings.\n\n" +
                "FitLife provides general wellness information, not medical advice."
        }
        setContentView(ScrollView(this).apply { addView(tv) })
    }
}
