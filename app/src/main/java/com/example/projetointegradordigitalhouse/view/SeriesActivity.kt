package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.example.projetointegradordigitalhouse.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.synnapps.carouselview.CarouselView

class SeriesActivity : AppCompatActivity() {

    private val imgsCharacters = intArrayOf(
        R.drawable.black_widow,
        R.drawable.iron_man,
        R.drawable.captain_marvel,
        R.drawable.spider_man,
        R.drawable.hero2
    )
    private val imgsComics = intArrayOf(
        R.drawable.comic2,
        R.drawable.comic3,
        R.drawable.comic4,
        R.drawable.comic5,
        R.drawable.comic6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)

        initComponents()


    }

    private fun initComponents() {
        findViewById<CarouselView>(R.id.cvSeriesCharacters).pageCount = imgsCharacters.size
        findViewById<CarouselView>(R.id.cvSeriesCharacters).setImageListener {
                position, imageView -> imageView.setImageResource(imgsCharacters[position])
        }
        findViewById<CarouselView>(R.id.cvSeriesComics).pageCount = imgsComics.size
        findViewById<CarouselView>(R.id.cvSeriesComics).setImageListener {
            position, imageView -> imageView.setImageResource(imgsComics[position])
        }
        findViewById<ImageButton>(R.id.ibSeriesSearch).setOnClickListener {
            startActivity(Intent(this,ChipSearchActivity::class.java))
        }

        findViewById<ImageButton>(R.id.ibSeriesFavoriteNO).setOnClickListener {
            findViewById<ImageButton>(R.id.ibSeriesFavoriteYes).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.ibSeriesFavoriteNO).visibility = View.INVISIBLE
        }
        findViewById<ImageButton>(R.id.ibSeriesFavoriteYes).setOnClickListener {
            findViewById<ImageButton>(R.id.ibSeriesFavoriteNO).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.ibSeriesFavoriteYes).visibility = View.INVISIBLE
        }

        findViewById<CarouselView>(R.id.cvSeriesCharacters).setImageClickListener {
            startActivity(Intent(this,CharacterActivity::class.java))
        }

        findViewById<CarouselView>(R.id.cvSeriesComics).setImageClickListener {
            startActivity(Intent(this,ComicActivity::class.java))
        }

        BottomNavigationView.OnNavigationItemReselectedListener {
            when (it.itemId){
                R.id.page_1 -> {
                    startActivity(Intent(this,HomeActivity::class.java))
                }
                R.id.page_2 -> {
//                    startActivity(this, FavoriteActivity::class.java)
                }
                R.id.page_3 ->{
                    startActivity(Intent(this,ChipSearchActivity::class.java))
                }
                R.id.page_4 -> {
//                    startActivity(this, ProfileActivity::class.java)
                }

            }
        }

    }
}