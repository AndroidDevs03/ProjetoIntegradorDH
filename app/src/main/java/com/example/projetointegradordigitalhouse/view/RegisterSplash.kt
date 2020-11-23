package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.projetointegradordigitalhouse.databinding.ActivityRegisterSplashBinding

class RegisterSplash : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 3000
    private lateinit var binding: ActivityRegisterSplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)

        binding.btHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }
}