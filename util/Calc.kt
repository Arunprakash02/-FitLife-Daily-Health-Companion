package com.fitlife.app.util

import kotlin.math.ceil

object Calc {
    /** 20 kg -> 1 L, 40 kg -> 2 L ... clamped to a sensible range. */
    fun waterLiters(weightKg: Float): Double = (weightKg / 20.0).coerceIn(1.0, 6.0)

    fun waterMl(weightKg: Float): Int = (waterLiters(weightKg) * 1000).toInt()

    fun glasses(goalMl: Int, glassMl: Int = 250): Int = ceil(goalMl / glassMl.toDouble()).toInt()

    /** Minutes between drinks when spread across the awake window. */
    fun smartIntervalMinutes(goalMl: Int, awakeHours: Int = 15, glassMl: Int = 250): Int =
        (awakeHours * 60) / glasses(goalMl, glassMl).coerceAtLeast(1)

    fun bmi(weightKg: Float, heightCm: Float): Double {
        val m = heightCm / 100.0
        return if (m <= 0) 0.0 else weightKg / (m * m)
    }

    fun bmiLabel(bmi: Double): String = when {
        bmi < 18.5 -> "Underweight range"
        bmi < 25 -> "Healthy range"
        bmi < 30 -> "Overweight range"
        else -> "Obesity range"
    }

    fun formatMinutes(min: Long): String =
        if (min >= 60) "${min / 60} h ${min % 60} min" else "$min min"

    val healthyFoods = listOf(
        "🥚 Eggs", "🐟 Fish & chicken", "🥬 Vegetables", "🍎 Fruits", "🫘 Dal, chickpeas, green gram",
        "🥛 Curd / milk", "🥜 Nuts & seeds", "🌾 Whole grains (millets, oats, brown rice)"
    )

    val limitFoods = listOf(
        "🍟 Deep fried food", "🥤 Sugary drinks", "🍬 Excess sweets", "🍔 Fast food",
        "🍪 Biscuits & bakery items", "🍞 White bread (large amounts)", "🍚 Very large rice portions",
        "📦 Highly processed snacks"
    )
}
