package com.example.projetointegradordigitalhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.synnapps.carouselview.CarouselView

class HomeActivity : AppCompatActivity() {

    private val imgsCharacters = intArrayOf(
        R.drawable.black_widow,
        R.drawable.iron_man,
        R.drawable.captain_marvel,
        R.drawable.spider_man,
        R.drawable.hero2
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
    private val imgsComics = intArrayOf(
        R.drawable.comic2,
        R.drawable.comic3,
        R.drawable.comic4,
        R.drawable.comic5,
        R.drawable.comic6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<CarouselView>(R.id.cvCharacter).pageCount = imgsCharacters.size
        findViewById<CarouselView>(R.id.cvCharacter).setImageListener {
                position, imageView ->
            imageView.setImageResource(imgsCharacters[position])
        }

        findViewById<CarouselView>(R.id.cvComics).pageCount = imgsComics.size
        findViewById<CarouselView>(R.id.cvComics).setImageListener {
                position, imageView ->
            imageView.setImageResource(imgsComics[position])
        }

        findViewById<CarouselView>(R.id.cvMovies).pageCount = imgFilmes.size
        findViewById<CarouselView>(R.id.cvMovies).setImageListener {
                position, imageView ->
            imageView.setImageResource(imgFilmes[position])
        }

        findViewById<CarouselView>(R.id.cvSeries).pageCount = imgsSeries.size
        findViewById<CarouselView>(R.id.cvSeries).setImageListener {
                position, imageView ->
            imageView.setImageResource(imgsSeries[position])
        }
    }

}