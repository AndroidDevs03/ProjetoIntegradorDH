package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityCharacterBinding
import com.example.projetointegradordigitalhouse.databinding.ActivitySeriesBinding
import com.example.projetointegradordigitalhouse.model.CharacterResult
import com.example.projetointegradordigitalhouse.model.SeriesResult
import com.example.projetointegradordigitalhouse.util.Constants
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_SERIE
import com.example.projetointegradordigitalhouse.viewModel.CharacterViewModel
import com.example.projetointegradordigitalhouse.viewModel.SeriesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.synnapps.carouselview.CarouselView
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.activity_series.*
import java.io.ByteArrayOutputStream

class SeriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeriesBinding
    private var serie : SeriesResult? = null
//    private var serieComics: List<Long>? = null
//    private var serieChars: List<Long>? = null
    private val firebaseAuth by lazy{ Firebase.auth }
    private val viewModel by lazy { SeriesViewModel(this) }


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
        binding = ActivitySeriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serie = intent.getParcelableExtra(KEY_INTENT_SERIE)

        initComponents()
    }

    private fun initComponents() {

        firebaseAuth?.let{ auth ->
            serie?.let{ serieResult ->

                serieResult.charactersList?.let{
                    viewModel.getSeriesCharacters(it)
                }
                serieResult.comicsList?.let{
                    viewModel.getSeriesComics(it)
                }
                serieResult.name?.let{
                    binding.tvSeriesTitle.text = it
                }
                serieResult.description?.let{
                    binding.tvSeriesDescription.text = it
                }
                serieResult.thumbnail?.let{
                    Glide.with(this).load(it).into(binding.ivSeriesPicture)
                }
            }
        }?: run{
            startActivity(Intent(this, LoginActivity::class.java))
        }

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

        findViewById<ImageButton>(R.id.ibSeriesShare).setOnClickListener {
            share()
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
    private fun share(){
        val intent : Intent = Intent(Intent.ACTION_SEND)
        intent.setType("image/png")
        val drawable : BitmapDrawable = ivSeriesPicture.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val bytes : ByteArrayOutputStream = ByteArrayOutputStream()
        val description : String = findViewById<TextView>(R.id.tvSeriesDescription).text.toString()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes)
        val path : String = MediaStore.Images.Media.insertImage(contentResolver,bitmap,"Favorite Character",null)
        val uri : Uri = Uri.parse(path)
        intent.putExtra(Intent.EXTRA_COMPONENT_NAME, "Introducing content previews")
        intent.putExtra(Intent.EXTRA_STREAM,uri)
        intent.putExtra(Intent.EXTRA_TEXT, description)
        startActivity(Intent.createChooser(intent,"Compartilhar nas redes sociais"))
    }
}