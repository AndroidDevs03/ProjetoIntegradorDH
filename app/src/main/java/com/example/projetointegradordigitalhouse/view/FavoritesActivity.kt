package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityFavoritesBinding
import com.example.projetointegradordigitalhouse.databinding.ActivityHomeBinding
import com.example.projetointegradordigitalhouse.viewModel.FavoritesViewModel
import com.example.projetointegradordigitalhouse.viewModel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val viewModel by lazy { FavoritesViewModel(this) }
    private val firebaseAuth by lazy{ Firebase.auth }
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var navigationView : NavigationView

    var tabPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        setupObservables()

    }

    private fun setupObservables() {

        binding.bnFavorites.setOnNavigationItemSelectedListener{
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

    private fun initComponents() {
        binding.bnFavorites.menu.getItem(1).setChecked(true).setEnabled(false)
        binding.tlFavorites.addTab(binding.tlFavorites.newTab().setText("Characters"))
        binding.tlFavorites.addTab(binding.tlFavorites.newTab().setText("Series"))
        binding.tlFavorites.addTab(binding.tlFavorites.newTab().setText("Comics"))

        firebaseAuth.currentUser?.let{
            viewModel.getFavoritesCharacters(it.uid)
            viewModel.getFavoritesSeries(it.uid)
            viewModel.getFavoritesComics(it.uid)
        }?: run{
            Toast.makeText(this,"Necessário fazer o login para acesso aos Favoritos",Toast.LENGTH_LONG).show()
            startActivity(Intent(this,HomeActivity::class.java))
        }

    }
}