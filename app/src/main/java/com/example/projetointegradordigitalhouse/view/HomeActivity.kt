package com.example.projetointegradordigitalhouse.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityHomeBinding
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_CHARACTER
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_COMIC
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_SEARCH
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_SERIES
import com.example.projetointegradordigitalhouse.viewModel.HomeViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.synnapps.carouselview.ImageListener
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

@Suppress("UNCHECKED_CAST", "SENSELESS_NULL_IN_WHEN")
class HomeActivity : AppCompatActivity() {

    private val viewModel by lazy { HomeViewModel(this) }
    private val firebaseAuth by lazy { Firebase.auth }
    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val firebaseFirestore by lazy {
        Firebase.firestore
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

        firebaseAuth.currentUser?.let {
            viewModel.getHomeCharacters()
            viewModel.getHomeSeries()
            viewModel.getHomeComics()
            viewModel.getSearchHistory()
        } ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
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
                    seriesList as MutableList<GeneralResult>
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
                viewModel.addSearchToLocalDatabase(
                    Search(
                        newtag,
                        "",
                        Date().toString()
                    )
                )
                val intent = Intent(this@HomeActivity, ChipSearchActivity::class.java)
                intent.putExtra(KEY_INTENT_SEARCH, newtag)
                startActivity(intent)
            }
        }
        binding.hmBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    firebaseAuth.currentUser?.let{
                        when(it.isAnonymous){
                            true -> {navigationView.menu.findItem(R.id.item1).isEnabled = false
                                     navigationView.menu.findItem(R.id.item2).isEnabled = false
                                     navigationView.menu.findItem(R.id.item3).isEnabled = false
                            }
                            false ->{
                                navigationView.menu.findItem(R.id.item4).isVisible = false
                            }

                        }
                        firebaseFirestore.collection("users").document(it.uid).get()
                            .addOnSuccessListener { snapshot ->
                                val userData = snapshot.data
                                val headerView = navigationView.getHeaderView(0)
                                val namePerfil = headerView.findViewById<TextView>(R.id.tvNamePerfil)
                                val emailPerfil = headerView.findViewById<TextView>(R.id.tvEmailPerfil)
                                val imagem = headerView.findViewById<CircleImageView>(R.id.ivAvatar)
                                when (userData) {
                                    null -> {
                                        Glide.with(this).load(Avatar.avatar[0]).into(imagem)
                                        namePerfil.text = "Anônimo"
                                        emailPerfil.text = ""
                                    }
                                    else -> {
                                        val position = userData["avatar_id"] as Number
                                        namePerfil.text = userData["name"] as String
                                        emailPerfil.text = userData["email"] as String
                                        Glide.with(this).load(Avatar.avatar[position.toInt()]).into(imagem)
                                    }
                                }

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
                    it.isEnabled = false
//                    startActivity(Intent(this, HomeActivity::class.java))
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
                    firebaseAuth.currentUser?.let{
                        if (!it.isAnonymous){
                            startActivity(Intent(this, RegisterActivity::class.java))
                            drawerLayout.close()
                        }
                    }
                    true
                }
                R.id.item2 ->{
                    firebaseAuth.currentUser?.let{
                        if (!it.isAnonymous){
                            startActivityForResult(Intent(this, PopUpWindow::class.java),100)
                        }
                    }
                    true
                }
                R.id.item3 ->{
                    firebaseAuth.currentUser?.let {
                        if(!it.isAnonymous ){
                            firebaseAuth.signOut()
                            finishAffinity()
                            startActivity(Intent(this, LoginActivity::class.java))
                            drawerLayout.close()
                        }
                    }
                    true
                }
                R.id.item4 ->{
                    firebaseAuth.currentUser?.let {
                        if(it.isAnonymous ){
                            firebaseAuth.signOut()
                            it.delete()
                            finishAffinity()
                            startActivity(Intent(this, LoginActivity::class.java))
                            drawerLayout.close()
                        }


                    }
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.cvCharacter.setImageClickListener {
            val intent = Intent(this, CharacterActivity::class.java)
            val temp = charsList[it]
            intent.putExtra(KEY_INTENT_CHARACTER, temp)
            startActivity(intent)
        }

        binding.cvComics.setImageClickListener {
            val intent = Intent(this, ComicActivity::class.java)
            val temp = comicsList[it]
            intent.putExtra(KEY_INTENT_COMIC, temp)
            startActivity(intent)
        }

        binding.cvSeries.setImageClickListener {
            val intent = Intent(this, SeriesActivity::class.java)
            val temp = seriesList[it]
            intent.putExtra(KEY_INTENT_SERIES, temp)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        val item = data?.getIntExtra("foto",0)
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth.currentUser?.let{
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
                            val position = userData["avatar_id"] as Number
                            Glide.with(this).load(Avatar.avatar[position.toInt()]).into(imagem)
                        }
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        } ?: run {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseAuth.currentUser?.let {
            if (it.isAnonymous){
                firebaseAuth.signOut()
                it.delete()
            }
        }
        viewModel.removeAllChips()
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