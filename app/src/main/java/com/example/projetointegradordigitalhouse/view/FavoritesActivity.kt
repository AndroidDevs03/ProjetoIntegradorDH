package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityFavoritesBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        BottomNavigationView.OnNavigationItemReselectedListener {
            when (it.itemId){
                R.id.page_1 -> {
                    startActivity(Intent(this,HomeActivity::class.java))
                }
                R.id.page_2 -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                }
                R.id.page_3 ->{
                    startActivity(Intent(this,ChipSearchActivity::class.java))
                }
                R.id.page_4 -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
         }
    }
}