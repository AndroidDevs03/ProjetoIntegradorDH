package com.example.projetointegradordigitalhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.synnapps.carouselview.CarouselView

class CharacterActivity : AppCompatActivity() {

    private val imgsComics = intArrayOf(
            R.drawable.comic2,
            R.drawable.comic3,
            R.drawable.comic4,
            R.drawable.comic5,
            R.drawable.comic6
    )
    private val imgsSeries = intArrayOf(
            R.drawable.daredevil_serie,
            R.drawable.ironfist_serie,
            R.drawable.jessica_serie,
            R.drawable.defenders_serie,
            R.drawable.luke_serie
    )
    private val imgFilmes = intArrayOf(
            R.drawable.avangers_filmes,
            R.drawable.captainamerica_filme,
            R.drawable.doctor_filmes,
            R.drawable.guardians_filme,
            R.drawable.doctor_filmes
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)

        initComponents()

    }

    private fun initComponents() {
        findViewById<CarouselView>(R.id.cvCharacterComics).pageCount = imgsComics.size
        findViewById<CarouselView>(R.id.cvCharacterComics).setImageListener {
            position, imageView -> imageView.setImageResource(imgsComics[position])
        }

        findViewById<CarouselView>(R.id.cvCharacterSeries).pageCount = imgsSeries.size
        findViewById<CarouselView>(R.id.cvCharacterSeries).setImageListener {
            position, imageView -> imageView.setImageResource(imgsSeries[position])
        }

        findViewById<CarouselView>(R.id.cvCharacterMovies).pageCount = imgFilmes.size
        findViewById<CarouselView>(R.id.cvCharacterMovies).setImageListener {
            position, imageView -> imageView.setImageResource(imgFilmes[position])
        }

        findViewById<ImageButton>(R.id.ibCharacterSearch).setOnClickListener {
            startActivity(Intent(this,ChipSearch::class.java))
        }

        findViewById<ImageButton>(R.id.ibCharacterFavoriteNO).setOnClickListener {
            findViewById<ImageButton>(R.id.ibCharacterFavoriteYes).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.ibCharacterFavoriteNO).visibility = View.INVISIBLE
        }
        findViewById<ImageButton>(R.id.ibCharacterFavoriteYes).setOnClickListener {
            findViewById<ImageButton>(R.id.ibCharacterFavoriteNO).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.ibCharacterFavoriteYes).visibility = View.INVISIBLE
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
                    startActivity(Intent(this,ChipSearch::class.java))
                }
                R.id.page_4 -> {
//                    startActivity(this, ProfileActivity::class.java)
                }

            }
        }

    }
}