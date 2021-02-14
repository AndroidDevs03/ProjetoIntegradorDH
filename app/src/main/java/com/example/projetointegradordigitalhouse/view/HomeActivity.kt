package com.example.projetointegradordigitalhouse.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityHomeBinding
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_SEARCH
import com.example.projetointegradordigitalhouse.viewModel.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.synnapps.carouselview.ImageClickListener
import com.synnapps.carouselview.ImageListener
import java.util.*
@Suppress("UNCHECKED_CAST")
class HomeActivity : AppCompatActivity() {

    private val viewModel by lazy { HomeViewModel(this) }

    private val imgsSeries = intArrayOf(
        R.drawable.daredevil_serie,
        R.drawable.ironfist_serie,
        R.drawable.jessica_serie,
        R.drawable.defenders_serie,
        R.drawable.luke_serie
    )
    private val imgsComics = intArrayOf(
        R.drawable.comic2,
        R.drawable.comic3,
        R.drawable.comic4,
        R.drawable.comic5,
        R.drawable.comic6
    )

    private lateinit var binding: ActivityHomeBinding

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView : NavigationView

//    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.dlPerfil
        navigationView = binding.nvPerfil


        initComponents()
        setupObservables()
    }

    private fun initComponents() {
        Log.i("HomeActivity", "InitComponents")

        viewModel.getHomeCharacters()
        viewModel.getHomeSeries()
        viewModel.getHomeComics()
        viewModel.getSearchHistory()

    }

    private fun setupObservables() {
        val charsList = mutableListOf<CharacterResult>()
        val seriesList = mutableListOf<SeriesResult>()
        val comicsList = mutableListOf<ComicResult>()
        Log.i("HomeActivity", "Setup Observables")

        viewModel.homeCharList.observe(this, {
            charsList.addAll(it)
            Log.i("CarouselView", "${charsList.size} Characters")
            binding.cvCharacter.setImageListener(
                CarouselListener(
                    this,
                    charsList as MutableList<GeneralResult>
                )
            )
            binding.cvCharacter.pageCount = charsList.size

        })
        viewModel.homeSeriesList.observe(this, {
            seriesList.addAll(it)
            Log.i("CarouselView", "${seriesList.size} Series")
            binding.cvSeries.setImageListener(
                CarouselListener(
                    this,
                    seriesList  as MutableList<GeneralResult>
                )
            )
            binding.cvSeries.pageCount = seriesList.size

        })
        viewModel.homeComicsList.observe(this, {
            comicsList.addAll(it)
            Log.i("CarouselView", "${comicsList.size} Comics")
            binding.cvComics.setImageListener(
                CarouselListener(
                    this,
                    comicsList as MutableList<GeneralResult>
                )
            )
            binding.cvComics.pageCount = comicsList.size

        })
        viewModel.lastSearchHistory.observe(this, {
            it?.let { searchTags ->
                val adapter = ArrayAdapter(this@HomeActivity, R.layout.list_item, searchTags)
                (binding.hmSearchField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })
        binding.hmSearchField.setEndIconOnClickListener {
            val newtag = binding.hmSearchField.editText?.text.toString().trim()
            if (newtag != "") {
                viewModel.addSearchToLocalDatabase(Search(newtag, "", Date().toString())) //todo: Arrumar aqui
                val intent = Intent(this@HomeActivity, ChipSearchActivity::class.java)
                intent.putExtra(KEY_INTENT_SEARCH, newtag)
                startActivity(intent)
            }
        }
        binding.hmBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
//                    startActivity(Intent(this, HomeActivity::class.java))
                    drawerLayout.open()
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
                else -> {
                    false
                }
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            when (menuItem.itemId){
                R.id.item1 ->{
                    drawerLayout.close()
                    true
                }
                R.id.item2 ->{
                    startActivityForResult(Intent(this, PopUpWindow::class.java),100)
//                    drawerLayout.close()
                    true
                }
                R.id.item3 ->{
                    Firebase.auth.signOut()
                    finishAffinity()
                    startActivity(Intent(this, LoginActivity::class.java))
//                    drawerLayout.close()
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.cvCharacter.setImageClickListener{
            val intent = Intent(this, CharacterActivity::class.java)
            intent.putExtra("blablabla", charsList[it].id)
            startActivity(intent)
        }

        binding.cvComics.setImageClickListener {
            startActivity(Intent(this, ComicActivity::class.java))
        }

        binding.cvSeries.setImageClickListener {
            startActivity(Intent(this, SeriesActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val item = data?.getIntExtra("foto",0)
    }
}

class CarouselListener(
    private val activity: Activity,
    private val dataList: MutableList<GeneralResult>
) : ImageListener {
    override fun setImageForPosition(position: Int, imageView: ImageView) {
        Glide.with(activity)
            .load(dataList[position].thumbnail)
            .fitCenter()
            .placeholder(R.drawable.button)
            .into(imageView)
    }
}