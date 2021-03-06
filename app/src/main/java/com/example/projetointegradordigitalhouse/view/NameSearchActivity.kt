package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityNameSearchBinding
import com.example.projetointegradordigitalhouse.model.Avatar
import com.example.projetointegradordigitalhouse.model.characters.Result
import com.example.projetointegradordigitalhouse.util.Constants.Api.KEY_ACTIVE_SEARCH
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_SEARCH
import com.example.projetointegradordigitalhouse.view.adapter.NameSearchAdapter
import com.example.projetointegradordigitalhouse.viewModel.NameSearchViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class NameSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNameSearchBinding
    private lateinit var viewModel: NameSearchViewModel
    private var characterList = mutableListOf<Result>()
    private val incomingSearch: String? by lazy {intent.getStringExtra(KEY_INTENT_SEARCH)}
    private lateinit var activeSearch: String

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val firebaseFirestore by lazy {
        Firebase.firestore
    }
    private val firebaseAuth by lazy{ Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        setupObservables()
    }
    private fun initComponents() {
        activeSearch = incomingSearch?: ""
        binding.nsSearchField.editText?.setText(activeSearch)
        viewModel = ViewModelProvider(this).get(NameSearchViewModel::class.java)
        refreshResults()
    }
    private fun setupObservables() {
        binding.nsSearchField.setEndIconOnClickListener {
            activeSearch = binding.nsSearchField.editText?.text.toString().trim()
            if (activeSearch!="") {
                binding.nsSearchField.editText?.text?.clear()
                characterList.clear()
                //viewModel.getCharactersByName(activeSearch)
            }
        }
        binding.nsBottomNavigation.setOnNavigationItemSelectedListener(){
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
        viewModel.searchCharList.observe(this, {
            it?.let { it ->
                characterList.addAll(it)
                binding.nsRvChars.apply {
                    layoutManager = LinearLayoutManager(this@NameSearchActivity,LinearLayoutManager.HORIZONTAL,false)
                    adapter = NameSearchAdapter(characterList) { position ->
                        val intent = Intent(this@NameSearchActivity, CharacterActivity::class.java)
                        intent.putExtra("blablabla", characterList[position])
                        startActivity(intent)
                    }
                }
            }
        })
    }

    private fun refreshResults() {
        characterList.clear()
        binding.nsRvChars.removeAllViewsInLayout()
        //viewModel.getCharactersByName(activeSearch)
    }
}