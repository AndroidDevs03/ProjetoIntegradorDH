package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
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

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        setupObservables()
    }

    private fun setupObservables() {
        binding.hmSearchField.setEndIconOnClickListener {
            val newtag = binding.hmSearchField.editText?.text.toString().trim()
            if (newtag!="") {
                        val intent = Intent(this@HomeActivity, ChipSearchActivity::class.java)
                        intent.putExtra(KEY_INTENT_SEARCH, newtag)
                        startActivity(intent)
            }
        }
        binding.hmBottomNavigation.setOnNavigationItemSelectedListener(){
            when(it.itemId){
                R.id.page_1 -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.page_2 -> {
                    //startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }
                R.id.page_3 -> {
                    startActivity(Intent(this, ChipSearchActivity::class.java))
                    true
                }
                R.id.page_4 -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                else -> {false}
            }
        }
    }

    private fun initComponents() {

        binding.cvCharacter.pageCount = imgsCharacters.size
        binding.cvCharacter.setImageListener { position, imageView ->
            imageView.setImageResource(imgsCharacters[position])
        }

        binding.cvComics.pageCount = imgsComics.size
        binding.cvComics.setImageListener { position, imageView ->
            imageView.setImageResource(imgsComics[position])
        }

        binding.cvMovies.pageCount = imgFilmes.size
        binding.cvMovies.setImageListener { position, imageView ->
            imageView.setImageResource(imgFilmes[position])
        }

        binding.cvSeries.pageCount = imgsSeries.size
        binding.cvSeries.setImageListener { position, imageView ->
            imageView.setImageResource(imgsSeries[position])
        }

        binding.cvCharacter.setImageClickListener {
            startActivity(Intent(this, CharacterActivity::class.java))
        }

        binding.cvComics.setImageClickListener {
            startActivity(Intent(this, ComicActivity::class.java))
        }

        binding.cvMovies.setImageClickListener {
            startActivity(Intent(this, MovieActivity::class.java))
        }

        binding.cvSeries.setImageClickListener {
            startActivity(Intent(this, SeriesActivity::class.java))
        }
    }
    companion object {
        const val KEY_INTENT_SEARCH = "search"
    }

}