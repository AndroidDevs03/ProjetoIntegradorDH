package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.insertImage
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityCharacterBinding
import com.example.projetointegradordigitalhouse.model.CharacterResult
import com.example.projetointegradordigitalhouse.model.ComicResult
import com.example.projetointegradordigitalhouse.model.GeneralResult
import com.example.projetointegradordigitalhouse.model.SeriesResult
import com.example.projetointegradordigitalhouse.util.Constants
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_CHARACTER
import com.example.projetointegradordigitalhouse.viewModel.CharacterViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.synnapps.carouselview.CarouselView
import kotlinx.android.synthetic.main.activity_character.*
import java.io.ByteArrayOutputStream

class CharacterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterBinding
    private var character : CharacterResult? = null
//    private var characterSeries: List<Long>? = null
    private val firebaseAuth by lazy{ Firebase.auth }
    private val viewModel by lazy { CharacterViewModel(this) }

    private var comicsList = mutableListOf<ComicResult>()
    private var seriesList = mutableListOf<SeriesResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        character = intent.getParcelableExtra(KEY_INTENT_CHARACTER)

        initComponents()

    }

    private fun initComponents() {

        firebaseAuth?.let{ auth ->
            character?.let{ charResult ->
                viewModel.getCharacterComics(charResult.id)
                charResult.series?.let{ seriesListID ->
                    viewModel.getCharacterSeries(seriesListID)
                }
                charResult.name?.let{
                    binding.tvCharacterTitle.text = it
                }
                charResult.description?.let{
                    binding.tvCharacterDescription.text = it
                }
                charResult.thumbnail?.let{
                    Glide.with(this).load(it).into(binding.ivCharacterPicture)
                }
                initSeries()
                initComics()
            }
        }?: run{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.cvCharacterComics.setImageClickListener {
            val intent = Intent(this, ComicActivity::class.java)
            val temp = comicsList[it]
            intent.putExtra(Constants.Intent.KEY_INTENT_COMIC,temp)
            startActivity(intent)
        }

        binding.cvCharacterSeries.setImageClickListener {
            val intent = Intent(this, SeriesActivity::class.java)
            val temp = seriesList[it]
            intent.putExtra(Constants.Intent.KEY_INTENT_SERIE,temp)
            startActivity(intent)
        }

//        findViewById<ImageButton>(R.id.ibCharacterFavorite).setOnClickListener {
//            findViewById<ImageButton>(R.id.ibCharacterFavorite).visibility = View.VISIBLE
//            findViewById<ImageButton>(R.id.ibCharacterFavorite).visibility = View.INVISIBLE
//        }

        findViewById<ImageButton>(R.id.ibCharacterShare).setOnClickListener {
            share()
        }

        findViewById<BottomNavigationView>(R.id.bnvCharacter).setOnNavigationItemSelectedListener {
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
    }

    private fun initSeries() {

        viewModel.charSeriesList.observe(
            this, {
            seriesList.addAll(it)
            Log.i("CarouselView", "${seriesList.size} Series")
            binding.cvCharacterSeries.setImageListener(
                CarouselListener(
                    this,
                    seriesList  as MutableList<GeneralResult>
                )
            )
            binding.cvCharacterSeries.pageCount = seriesList.size

        })
    }

    private fun initComics() {

        viewModel.charComicsList.observe(
            this, {
                comicsList.addAll(it)
                Log.i("CarouselView", "${comicsList.size} Comics")
                binding.cvCharacterComics.setImageListener(
                    CarouselListener(
                        this,
                        comicsList  as MutableList<GeneralResult>
                    )
                )
                binding.cvCharacterComics.pageCount = comicsList.size

            })
    }

    private fun share(){
        val intent : Intent = Intent(Intent.ACTION_SEND)
        intent.setType("image/png")
        val drawable : BitmapDrawable  = ivCharacterPicture.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val bytes : ByteArrayOutputStream  = ByteArrayOutputStream()
        val description : String = findViewById<TextView>(R.id.tvCharacterDescription).text.toString()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes)
        val path : String = MediaStore.Images.Media.insertImage(contentResolver,bitmap,"Favorite Character",null)
        val uri :Uri = Uri.parse(path)
        intent.putExtra(Intent.EXTRA_COMPONENT_NAME, "Introducing content previews")
        intent.putExtra(Intent.EXTRA_STREAM,uri)
        intent.putExtra(Intent.EXTRA_TEXT, description)
        startActivity(Intent.createChooser(intent,"Compartilhar nas redes sociais"))
    }
}