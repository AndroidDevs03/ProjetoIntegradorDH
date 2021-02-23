package com.example.projetointegradordigitalhouse.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityFavoritesBinding
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.util.Constants
import com.example.projetointegradordigitalhouse.view.adapter.ComicAdapter
import com.example.projetointegradordigitalhouse.view.adapter.SerieAdapter
import com.example.projetointegradordigitalhouse.viewModel.FavoritesViewModel
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.text.FieldPosition

class FavoritesActivity  : AppCompatActivity() {
    private val firebaseAuth by lazy{ Firebase.auth }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView : NavigationView
    private val viewModel by lazy { FavoritesViewModel(this) }

    private val firebaseFirestore by lazy {
        Firebase.firestore
    }
    var tabPosition: Int = 0

    var character = listOf<CharacterResult>()
    var serie = listOf<SeriesResult>()
    var comic = listOf<ComicResult>()

    private lateinit var binding: ActivityFavoritesBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.dlPerfil
        navigationView = binding.nvPerfil

        setupListeners()
        initComponents()
//        updateRecyclerView(tabPosition,Triple(character,comic,serie))
        setupObservables()
    }

//    private fun setupObservables() {
//
//        viewModel.charList.observe(this, {
//            it?.let { favorites ->
//                updateRecyclerView(tabPosition,favorites as Triple<MutableList<CharacterResult>, MutableList<ComicResult>, MutableList<SeriesResult>>)
//            }
//        })
//    }
    private fun setupObservables(){
        viewModel.charList.observe(this,{
            it?.let {
                character = it
                updateRecyclerView(tabPosition,Triple(character,comic,serie))
            }
        })
        viewModel.seriesList.observe(this,{
            it?.let {
                serie = it
                updateRecyclerView(tabPosition,Triple(character,comic,serie))
            }
        })
        viewModel.comicsList.observe(this,{
            it?.let {
                comic = it
                updateRecyclerView(tabPosition,Triple(character,comic,serie))
            }
        })
    }

    private fun initComponents() {
        binding.fvTabLayout.addTab(binding.fvTabLayout.newTab().setText("Characters"))
        binding.fvTabLayout.addTab(binding.fvTabLayout.newTab().setText("Comics"))
        binding.fvTabLayout.addTab(binding.fvTabLayout.newTab().setText("Series"))
        viewModel.getCharacterFav()
        viewModel.getComicsFav()
        viewModel.getSeriesFav()
    }

    private fun setupListeners() {

        binding.fvTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab?.position ?: 0
                viewModel.apply {
                    charList.value?.let {
                        character = it
                        updateRecyclerView(tabPosition,Triple(character,comic,serie))
                    }
                    seriesList.value?.let {
                        serie = it
                        updateRecyclerView(tabPosition,Triple(character,comic,serie))
                    }
                    comicsList.value?.let {
                        comic = it
                        updateRecyclerView(tabPosition,Triple(character,comic,serie))
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.fvBottomNavigation.menu.getItem(1).setChecked(true).setEnabled(false)

        binding.fvBottomNavigation.setOnNavigationItemSelectedListener{
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
                    it.isEnabled = false
//                    startActivity(Intent(this, FavoritesActivity::class.java))
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
    }

    private fun updateRecyclerView(tab: Int,resultLists : Triple<List<CharacterResult>?, List<ComicResult>?, List<SeriesResult>?>){
        when (tab){
            0 -> {
                binding.fvRecyclerView.apply {
                    layoutManager = GridLayoutManager(this@FavoritesActivity, 2)
                    adapter = CharacterAdapter(viewModel, resultLists.first)
                }
            }
            1 -> {
                binding.fvRecyclerView.apply {
                    layoutManager = GridLayoutManager(this@FavoritesActivity, 2)
                    adapter = ComicAdapter(viewModel,resultLists.second)
                }
            }
            2 -> {
                binding.fvRecyclerView.apply {
                    layoutManager = GridLayoutManager(this@FavoritesActivity, 2)
                    adapter = SerieAdapter(viewModel,resultLists.third)
                }
            }
        }
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
        }?: run {

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
    }
}