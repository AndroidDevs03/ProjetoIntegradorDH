package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.example.projetointegradordigitalhouse.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.synnapps.carouselview.CarouselView

class MovieActivity : AppCompatActivity() {

    private val imgsCharacters = intArrayOf(
        R.drawable.black_widow,
        R.drawable.iron_man,
        R.drawable.captain_marvel,
        R.drawable.spider_man,
        R.drawable.hero2
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        initComponents()
    }

    private fun initComponents() {
        findViewById<CarouselView>(R.id.cvMovieCharacters).pageCount = imgsCharacters.size
        findViewById<CarouselView>(R.id.cvMovieCharacters).setImageListener {
                position, imageView ->
            imageView.setImageResource(imgsCharacters[position])
        }

        findViewById<ImageButton>(R.id.ibMovieSearch).setOnClickListener {
            startActivity(Intent(this,ChipSearchActivity::class.java))
        }

        findViewById<ImageButton>(R.id.ibMovieFavoriteNO).setOnClickListener {
            findViewById<ImageButton>(R.id.ibMovieFavoriteYes).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.ibMovieFavoriteNO).visibility = View.INVISIBLE
        }
        findViewById<ImageButton>(R.id.ibMovieFavoriteYes).setOnClickListener {
            findViewById<ImageButton>(R.id.ibMovieFavoriteNO).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.ibMovieFavoriteYes).visibility = View.INVISIBLE
        }

        findViewById<CarouselView>(R.id.cvMovieCharacters).setImageClickListener {
            startActivity(Intent(this,CharacterActivity::class.java))
        }

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