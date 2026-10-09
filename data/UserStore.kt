package com.fitlife.app.data

import android.content.Context

/** Simple profile storage using SharedPreferences. */
class UserStore(context: Context) {
    private val sp = context.applicationContext.getSharedPreferences("fitlife_user", Context.MODE_PRIVATE)

    var onboarded: Boolean
        get() = sp.getBoolean("onboarded", false)
        set(v) = sp.edit().putBoolean("onboarded", v).apply()

    var name: String
        get() = sp.getString("name", "") ?: ""
        set(v) = sp.edit().putString("name", v).apply()

    var age: Int
        get() = sp.getInt("age", 25)
        set(v) = sp.edit().putInt("age", v).apply()

    var heightCm: Float
        get() = sp.getFloat("height", 170f)
        set(v) = sp.edit().putFloat("height", v).apply()

    var weightKg: Float
        get() = sp.getFloat("weight", 60f)
        set(v) = sp.edit().putFloat("weight", v).apply()

    var goal: String
        get() = sp.getString("goal", "Maintain weight") ?: "Maintain weight"
        set(v) = sp.edit().putString("goal", v).apply()

    var trustedName: String
        get() = sp.getString("trusted_name", "") ?: ""
        set(v) = sp.edit().putString("trusted_name", v).apply()

    var trustedPhone: String
        get() = sp.getString("trusted_phone", "") ?: ""
        set(v) = sp.edit().putString("trusted_phone", v).apply()

    var stepGoal: Int
        get() = sp.getInt("step_goal", 8000)
        set(v) = sp.edit().putInt("step_goal", v).apply()

    /** 0 = off, 1 = every hour, 2 = every 2 hours */
    var reminderHours: Int
        get() = sp.getInt("reminder_hours", 2)
        set(v) = sp.edit().putInt("reminder_hours", v).apply()
}
