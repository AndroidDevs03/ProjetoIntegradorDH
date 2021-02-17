package com.example.projetointegradordigitalhouse.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityFavoritesBinding
import com.example.projetointegradordigitalhouse.databinding.ActivityHomeBinding
import com.example.projetointegradordigitalhouse.model.Avatar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class FavoritesActivity : AppCompatActivity() {
    private val firebaseAuth by lazy{ Firebase.auth }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView : NavigationView

    private val firebaseFirestore by lazy {
        Firebase.firestore
    }
    private lateinit var binding: ActivityFavoritesBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.dlPerfil
        navigationView = binding.nvPerfil

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