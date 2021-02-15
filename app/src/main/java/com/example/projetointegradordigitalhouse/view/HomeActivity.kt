package com.example.projetointegradordigitalhouse.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityHomeBinding
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_SEARCH
import com.example.projetointegradordigitalhouse.viewModel.HomeViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.synnapps.carouselview.ImageClickListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.synnapps.carouselview.ImageListener
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
@Suppress("UNCHECKED_CAST", "SENSELESS_NULL_IN_WHEN")
class HomeActivity : AppCompatActivity() {

    private val viewModel by lazy { HomeViewModel(this) }
    private val firebaseAuth by lazy{ Firebase.auth }
    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView : NavigationView

    private val firebaseFirestore by lazy {
        Firebase.firestore
    }

    private val auth by lazy {
        Firebase.auth
    }

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

        firebaseAuth.currentUser?.let{
            viewModel.getHomeCharacters()
            viewModel.getHomeSeries()
            viewModel.getHomeComics()
            viewModel.getSearchHistory()
        }?: run{
            startActivity(Intent(this, LoginActivity::class.java))
        }
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
                    auth.currentUser?.let{
                        firebaseFirestore.collection("users").document(it.uid).get()
                            .addOnSuccessListener { snapshot ->
                                val userData = snapshot.data
                                val headerView = navigationView.getHeaderView(0)
                                val namePerfil = headerView.findViewById<TextView>(R.id.tvNamePerfil)
                                val emailPerfil = headerView.findViewById<TextView>(R.id.tvEmailPerfil)
                                val imagem = headerView.findViewById<CircleImageView>(R.id.ivAvatar)
                                val position = userData?.get("avatar_id") as Number
                                namePerfil.text = userData?.get("name") as String
                                emailPerfil.text = userData?.get("email") as String
                                Glide.with(this).load(Avatar.avatar[position.toInt()]).into(imagem)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                    }?: run {

                    }

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
                    startActivity(Intent(this, HomeActivity::class.java))

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
                    startActivity(Intent(this, RegisterActivity::class.java))
                    drawerLayout.close()
                    true
                }
                R.id.item2 ->{
                    startActivityForResult(Intent(this, PopUpWindow::class.java),100)
//                    drawerLayout.close()
                    true
                }
                R.id.item3 ->{
                    if(auth.currentUser?.isAnonymous == false){
                        Firebase.auth.signOut()
                        finishAffinity()
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        navigationView.menu.findItem(R.id.item3).isEnabled = false
                    }
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
//        val item = data?.getIntExtra("foto",0)
    }

    override fun onResume() {
        super.onResume()
        auth.currentUser?.let{
            firebaseFirestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { snapshot ->
                    val userData = snapshot.data
                    val headerView = navigationView.getHeaderView(0)
                    val imagem = headerView.findViewById<CircleImageView>(R.id.ivAvatar)
                    when (userData) {
                        null -> {
                            Glide.with(this).load(Avatar.avatar[0]).into(imagem)
                        }
                        else -> {
                            val position = userData?.get("avatar_id") as Number
                            Glide.with(this).load(Avatar.avatar[position.toInt()]).into(imagem)
                        }
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }?: run {

        }
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