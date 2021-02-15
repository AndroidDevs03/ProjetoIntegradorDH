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
import com.example.projetointegradordigitalhouse.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.synnapps.carouselview.CarouselView
import kotlinx.android.synthetic.main.activity_character.*
import kotlinx.android.synthetic.main.activity_comic.*
import java.io.ByteArrayOutputStream

class ComicActivity : AppCompatActivity() {

    private val imgsCharacters = intArrayOf(
        R.drawable.black_widow,
        R.drawable.iron_man,
        R.drawable.captain_marvel,
        R.drawable.spider_man,
        R.drawable.hero2
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comic)

        initComponents()

    }

    private fun initComponents() {


        findViewById<CarouselView>(R.id.cvComicCharacters).pageCount = imgsCharacters.size
        findViewById<CarouselView>(R.id.cvComicCharacters).setImageListener {
                position, imageView ->
            imageView.setImageResource(imgsCharacters[position])
        }

        findViewById<ImageButton>(R.id.ibComicSearch).setOnClickListener {
            startActivity(Intent(this,ChipSearchActivity::class.java))
        }

        findViewById<ImageButton>(R.id.ibComicFavoriteNO).setOnClickListener {
            findViewById<ImageButton>(R.id.ibComicFavoriteYes).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.ibComicFavoriteNO).visibility = View.INVISIBLE
        }
        findViewById<ImageButton>(R.id.ibComicFavoriteYes).setOnClickListener {
            findViewById<ImageButton>(R.id.ibComicFavoriteNO).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.ibComicFavoriteYes).visibility = View.INVISIBLE
        }

        findViewById<CarouselView>(R.id.cvComicCharacters).setImageClickListener {
            startActivity(Intent(this,CharacterActivity::class.java))
        }
        findViewById<ImageButton>(R.id.ibComicShare).setOnClickListener {
            share()
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
        val drawable : BitmapDrawable = ivComicPicture.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val bytes : ByteArrayOutputStream = ByteArrayOutputStream()
        val description : String = findViewById<TextView>(R.id.tvComicDescription).text.toString()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes)
        val path : String = MediaStore.Images.Media.insertImage(contentResolver,bitmap,"Favorite Character",null)
        val uri : Uri = Uri.parse(path)
        intent.putExtra(Intent.EXTRA_COMPONENT_NAME, "Introducing content previews")
        intent.putExtra(Intent.EXTRA_STREAM,uri)
        intent.putExtra(Intent.EXTRA_TEXT, description)
        startActivity(Intent.createChooser(intent,"Compartilhar nas redes sociais"))
    }

}