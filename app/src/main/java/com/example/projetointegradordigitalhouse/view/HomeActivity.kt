package com.example.projetointegradordigitalhouse.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityMainBinding
import com.example.projetointegradordigitalhouse.model.characters.Result
import com.example.projetointegradordigitalhouse.util.Constants.Api.KEY_INTENT_SEARCH
import com.example.projetointegradordigitalhouse.viewModel.HomeViewModel
import com.synnapps.carouselview.ImageClickListener
import com.synnapps.carouselview.ImageListener

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
    private lateinit var viewModel: HomeViewModel
    private val characterList = mutableListOf<Result>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf("Material", "Design", "Components", "Android")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        (binding.hmSearchField.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        loadContent()
        setupObservables()
    }

    private fun loadContent() {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getHomeCharacters()
        viewModel.homeCharList.observe(this, {
            characterList.addAll(it)
            initComponents()
        })

    }

    private fun initComponents() {
        val imageListener = CharCarouselListener(this,characterList)

        binding.cvCharacter.setImageListener(imageListener)
        binding.cvCharacter.pageCount = characterList.size

        binding.cvComics.setImageListener { position, imageView ->
            imageView.setImageResource(imgsComics[position])
        }
        binding.cvComics.pageCount = imgsComics.size


        binding.cvMovies.setImageListener { position, imageView ->
            imageView.setImageResource(imgFilmes[position])
        }
        binding.cvMovies.pageCount = imgFilmes.size


        binding.cvSeries.setImageListener { position, imageView ->
            imageView.setImageResource(imgsSeries[position])
        }
        binding.cvSeries.pageCount = imgsSeries.size
    }

    private fun setupObservables() {
        binding.hmSearchField.setEndIconOnClickListener {
            val newtag = binding.hmSearchField.editText?.text.toString().trim()
            if (newtag!="") {
                        val intent = Intent(this@HomeActivity, NameSearchActivity::class.java)
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
                    startActivity(Intent(this, FavoritesActivity::class.java))
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
        binding.cvCharacter.setImageClickListener(ImageClickListener() {
            fun onClick(position: Int) {
                val intent = Intent(this, CharacterActivity::class.java)
                intent.putExtra("blablabla", characterList[position].id)
                startActivity(intent)
            }
        })

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
}
class CharCarouselListener(val activity: Activity, val charlist: MutableList<Result>): ImageListener{
    override fun setImageForPosition(position: Int, imageView: ImageView) {
        Glide.with(activity)
            .load(charlist[position].thumbnail.getThumb())
            .fitCenter()
            .placeholder(R.drawable.button)
            .into(imageView)
    }
}