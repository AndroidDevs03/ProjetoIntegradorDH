package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.projetointegradordigitalhouse.R


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler(Looper.getMainLooper()).postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            startActivity(Intent(this, HomeActivity::class.java))

            // close this activity
            finish()
        }, 2000)
    }
}