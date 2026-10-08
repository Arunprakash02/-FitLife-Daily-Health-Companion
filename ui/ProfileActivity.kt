package com.fitlife.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fitlife.app.data.UserStore
import com.fitlife.app.databinding.ActivityProfileBinding
import com.fitlife.app.util.Calc

class ProfileActivity : AppCompatActivity() {

    private lateinit var b: ActivityProfileBinding
    private lateinit var store: UserStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(b.root)
        store = UserStore(this)

        b.etWeight.setText(store.weightKg.toString())
        b.etStepGoal.setText(store.stepGoal.toString())
        render()

        b.btnSave.setOnClickListener {
            val w = b.etWeight.text.toString().toFloatOrNull()
            val g = b.etStepGoal.text.toString().toIntOrNull()
            if (w == null || w < 10 || w > 300 || g == null || g < 1000 || g > 50000) {
                Toast.makeText(this, "Enter a valid weight and step goal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            store.weightKg = w
            store.stepGoal = g
            render()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun render() {
        b.tvProfileName.text = store.name
        b.tvProfileInfo.text = "${store.age} yrs • ${store.heightCm.toInt()} cm • ${store.weightKg} kg • ${store.goal}"
        val bmi = Calc.bmi(store.weightKg, store.heightCm)
        b.tvBmi.text = "BMI %.1f  (%s)".format(bmi, Calc.bmiLabel(bmi))
        b.tvTrusted.text = if (store.trustedName.isBlank()) "Trusted person: not set"
        else "Trusted person: ${store.trustedName} ${store.trustedPhone}"
    }
}
