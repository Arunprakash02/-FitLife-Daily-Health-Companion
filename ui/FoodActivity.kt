package com.fitlife.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fitlife.app.databinding.ActivityFoodBinding
import com.fitlife.app.util.Calc

class FoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityFoodBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.tvHealthyList.text = Calc.healthyFoods.joinToString("\n")
        b.tvLimitList.text = Calc.limitFoods.joinToString("\n")
    }
}
