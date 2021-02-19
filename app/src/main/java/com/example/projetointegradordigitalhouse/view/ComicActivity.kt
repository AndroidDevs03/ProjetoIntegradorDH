package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityComicBinding
import com.example.projetointegradordigitalhouse.model.CharacterResult
import com.example.projetointegradordigitalhouse.model.ComicResult
import com.example.projetointegradordigitalhouse.model.GeneralResult
import com.example.projetointegradordigitalhouse.model.Search
import com.example.projetointegradordigitalhouse.util.Constants
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_COMIC
import com.example.projetointegradordigitalhouse.viewModel.CharacterViewModel
import com.example.projetointegradordigitalhouse.viewModel.ComicViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.synnapps.carouselview.CarouselView
import kotlinx.android.synthetic.main.activity_character.*
import kotlinx.android.synthetic.main.activity_comic.*
import java.io.ByteArrayOutputStream
import java.util.*

class ComicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComicBinding
    private var comic : ComicResult? = null
//    private var comicChars: List<Long>? = null
    private val firebaseAuth by lazy{ Firebase.auth }
    private val viewModel by lazy { ComicViewModel(this) }

    private var charsList = mutableListOf<CharacterResult>()


    private val imgsCharacters = intArrayOf(
        R.drawable.black_widow,
        R.drawable.iron_man,
        R.drawable.captain_marvel,
        R.drawable.spider_man,
        R.drawable.hero2
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityComicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        comic = intent.getParcelableExtra(KEY_INTENT_COMIC)
        initComponents()
        setupObservables()

    }

    private fun setupObservables() {
        viewModel.lastSearchHistory.observe(this, {
            it?.let { searchTags ->
                val adapter = ArrayAdapter(this@ComicActivity, R.layout.list_item, searchTags)
                (binding.codSearchField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })
        binding.codSearchField.setEndIconOnClickListener {
            val newtag = binding.codSearchField.editText?.text.toString().trim()
            if (newtag != "") {
                viewModel.addSearchToLocalDatabase(
                    Search(
                        newtag,
                        "",
                        Date().toString()
                    )
                )
                val intent = Intent(this@ComicActivity, ChipSearchActivity::class.java)
                intent.putExtra(Constants.Intent.KEY_INTENT_SEARCH, newtag)
                startActivity(intent)
            }
        }
    }

    private fun initComponents() {
        viewModel.getSearchHistory()

//        firebaseAuth?.let{ auth ->
            comic?.let{ comicResult ->
                comicResult.charactersList?.let { charactersListID ->
                  viewModel.getComicCharacters(charactersListID)
              }
                comicResult.name?.let{
                    binding.tvComicTitle.text = it
                }
                comicResult.description?.let{
                    binding.tvComicDescription.text = it
                }
                comicResult.thumbnail?.let{
                    Glide.with(this).load(it).into(binding.ivComicPicture)
                }
                comicResult.published?.let{
                    binding.tvComicPublishedDate.text = it
                }
                if (firebaseAuth.currentUser?.isAnonymous?.not() == true){
                    Log.i("RecyclerView", "Usuário identificado")
                    binding.ibComicFavorite.isSelected = comicResult.favoriteTagFlag
                    binding.ibComicFavorite.setOnClickListener {
                        if (binding.ibComicFavorite.isSelected){
                            favoriteClicked(false) // true = adicionar, false = remover
                            binding.ibComicFavorite.isSelected = false
                        } else {
                            favoriteClicked(true) // true = adicionar, false = remover
                            binding.ibComicFavorite.isSelected = true
                        }
                    }
                    binding.ibComicFavorite.isActivated = true
                }else{
                    Log.i("RecyclerView", "Usuário anônimo")
                    binding.ibComicFavorite.isActivated = false
                    binding.ibComicFavorite.setOnClickListener {
                        Toast.makeText(binding.ibComicFavorite.context, "Favorite is not allowed for unregistered users. Please sign in.", Toast.LENGTH_LONG).show()
                    }
                }
                initCharacters()

            }
//        }?: run{
//            startActivity(Intent(this, LoginActivity::class.java))
//        }

//        findViewById<ImageButton>(R.id.ibComicSearch).setOnClickListener {
//            startActivity(Intent(this,ChipSearchActivity::class.java))
//        }
//
//        findViewById<ImageButton>(R.id.ibComicFavoriteNO).setOnClickListener {
//            findViewById<ImageButton>(R.id.ibComicFavoriteYes).visibility = View.VISIBLE
//            findViewById<ImageButton>(R.id.ibComicFavoriteNO).visibility = View.INVISIBLE
//        }
//        findViewById<ImageButton>(R.id.ibComicFavoriteYes).setOnClickListener {
//            findViewById<ImageButton>(R.id.ibComicFavoriteNO).visibility = View.VISIBLE
//            findViewById<ImageButton>(R.id.ibComicFavoriteYes).visibility = View.INVISIBLE
//        }

        findViewById<CarouselView>(R.id.cvComicCharacters).setImageClickListener {
            val intent = Intent(this, CharacterActivity::class.java)
            val temp = charsList[it]
            intent.putExtra(Constants.Intent.KEY_INTENT_CHARACTER,temp)
            startActivity(intent)
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

    private fun initCharacters() {
        viewModel.comicCharList.observe(
            this, {
                charsList.addAll(it)
                Log.i("CarouselView", "${charsList.size} Characters")
                binding.cvComicCharacters.setImageListener(
                    CarouselListener(
                        this,
                        charsList  as MutableList<GeneralResult>
                    )
                )
                binding.cvComicCharacters.pageCount = charsList.size

            }
        )    }

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
    private fun favoriteClicked(add: Boolean) {
        if (add){ comic?.let { viewModel.addFavorite(it,1) } }
        else { comic?.let { viewModel.remFavorite(it,1) } }
    }

}