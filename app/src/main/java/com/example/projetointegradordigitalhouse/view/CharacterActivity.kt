package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityCharacterBinding
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.util.Constants
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_CHARACTER
import com.example.projetointegradordigitalhouse.viewModel.CharacterViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_character.*
import java.io.ByteArrayOutputStream
import java.util.*

class CharacterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterBinding
    private var character : CharacterResult? = null
//    private var characterSeries: List<Long>? = null
    private val firebaseAuth by lazy{ Firebase.auth }
    private val viewModel by lazy { CharacterViewModel(this) }

    private var comicsList = mutableListOf<ComicResult>()
    private var seriesList = mutableListOf<SeriesResult>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val firebaseFirestore by lazy {
        Firebase.firestore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        character = intent.getParcelableExtra(KEY_INTENT_CHARACTER)

        initComponents()
        setupObservables()

    }

    private fun setupObservables() {
        viewModel.lastSearchHistory.observe(this, {
            it?.let { searchTags ->
                val adapter = ArrayAdapter(this@CharacterActivity, R.layout.list_item, searchTags)
                (binding.chdSearchField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })
        binding.chdSearchField.setEndIconOnClickListener {
            val newtag = binding.chdSearchField.editText?.text.toString().trim()
            if (newtag != "") {
                viewModel.addSearchToLocalDatabase(
                    Search(
                        newtag,
                        "",
                        Date().toString()
                    )
                )
                val intent = Intent(this@CharacterActivity, ChipSearchActivity::class.java)
                intent.putExtra(Constants.Intent.KEY_INTENT_SEARCH, newtag)
                startActivity(intent)
            }
        }
    }

    private fun initComponents() {
        viewModel.getSearchHistory()

        //firebaseAuth?.let{ auth ->
            character?.let{ charResult ->
                viewModel.getCharacterComics(charResult.id)
                charResult.series?.let{ seriesListID ->
                    viewModel.getCharacterSeries(seriesListID)
                }
                charResult.name?.let{
                    binding.tvCharacterTitle.text = it
                }
                charResult.description?.let{
                    binding.tvCharacterDescription.text = it
                }
                charResult.thumbnail?.let{
                    Glide.with(this).load(it).into(binding.ivCharacterPicture)
                }
                //Configurando o botão de Favoritos
                if (firebaseAuth.currentUser?.isAnonymous?.not() == true){
                    Log.i("RecyclerView", "Usuário identificado")
                    binding.ibCharacterFavorite.isSelected = charResult.favoriteTagFlag
                    binding.ibCharacterFavorite.setOnClickListener {
                        if (binding.ibCharacterFavorite.isSelected){
                            favoriteClicked(false) // true = adicionar, false = remover
                            binding.ibCharacterFavorite.isSelected = false
                        } else {
                            favoriteClicked(true) // true = adicionar, false = remover
                            binding.ibCharacterFavorite.isSelected = true
                        }
                    }
                    binding.ibCharacterFavorite.isActivated = true
                }else{
                    Log.i("RecyclerView", "Usuário anônimo")
                    binding.ibCharacterFavorite.isActivated = false
                    binding.ibCharacterFavorite.setOnClickListener {
                        Toast.makeText(binding.ibCharacterFavorite.context, "Favorite is not allowed for unregistered users. Please sign in.", Toast.LENGTH_LONG).show()
                    }
                }
                //Configurando o botão de Search
                binding.ibCharacterSearch.isSelected = charResult.searchTagFlag
                binding.ibCharacterSearch.setOnClickListener {
                    if (binding.ibCharacterSearch.isSelected){
                        searchClicked(false) // true = adicionar, false = remover
                        binding.ibCharacterSearch.isSelected = false
                    } else {
                        searchClicked(true) // true = adicionar, false = remover
                        binding.ibCharacterSearch.isSelected = true
                    }
                }
                initSeries()
                initComics()
            }
        //}?: run{
            //startActivity(Intent(this, LoginActivity::class.java))
        //}

        binding.cvCharacterComics.setImageClickListener {
            val intent = Intent(this, ComicActivity::class.java)
            val temp = comicsList[it]
            intent.putExtra(Constants.Intent.KEY_INTENT_COMIC,temp)
            startActivity(intent)
        }

        binding.cvCharacterSeries.setImageClickListener {
            val intent = Intent(this, SeriesActivity::class.java)
            val temp = seriesList[it]
            intent.putExtra(Constants.Intent.KEY_INTENT_SERIES,temp)
            startActivity(intent)
        }


//        findViewById<ImageButton>(R.id.ibCharacterFavorite).setOnClickListener {
//            findViewById<ImageButton>(R.id.ibCharacterFavorite).visibility = View.VISIBLE
//            findViewById<ImageButton>(R.id.ibCharacterFavorite).visibility = View.INVISIBLE
//        }

        findViewById<ImageButton>(R.id.ibCharacterShare).setOnClickListener {
            share()
        }

        findViewById<BottomNavigationView>(R.id.bnvCharacter).setOnNavigationItemSelectedListener {
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
    }

    private fun searchClicked(add: Boolean) {
        if (add){ character?.let { viewModel.addSearchTag("${it.id}_${it.name}",0) }}
        else { character?.let {viewModel.removeSearchTag("${it.id}_${it.name}",0) }}
    }

    private fun favoriteClicked(add: Boolean) {
        if (add){ character?.let { viewModel.addFavorite(it,0) } }
        else { character?.let { viewModel.remFavorite(it,0) } }
    }

    private fun initSeries() {

        viewModel.charSeriesList.observe(
            this, {
            seriesList.addAll(it)
            Log.i("CarouselView", "${seriesList.size} Series")
            binding.cvCharacterSeries.setImageListener(
                CarouselListener(
                    this,
                    seriesList  as MutableList<GeneralResult>
                )
            )
            binding.cvCharacterSeries.pageCount = seriesList.size

        })
    }

    private fun initComics() {

        viewModel.charComicsList.observe(
            this, {
                comicsList.addAll(it)
                Log.i("CarouselView", "${comicsList.size} Comics")
                binding.cvCharacterComics.setImageListener(
                    CarouselListener(
                        this,
                        comicsList  as MutableList<GeneralResult>
                    )
                )
                binding.cvCharacterComics.pageCount = comicsList.size

            })
    }

    private fun share(){
        val intent : Intent = Intent(Intent.ACTION_SEND)
        intent.setType("image/png")
        val drawable : BitmapDrawable  = ivCharacterPicture.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val bytes : ByteArrayOutputStream  = ByteArrayOutputStream()
        val description : String = findViewById<TextView>(R.id.tvCharacterDescription).text.toString()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes)
        val path : String = MediaStore.Images.Media.insertImage(contentResolver,bitmap,"Favorite Character",null)
        val uri :Uri = Uri.parse(path)
        intent.putExtra(Intent.EXTRA_COMPONENT_NAME, "Introducing content previews")
        intent.putExtra(Intent.EXTRA_STREAM,uri)
        intent.putExtra(Intent.EXTRA_TEXT, description)
        startActivity(Intent.createChooser(intent,"Compartilhar nas redes sociais"))
    }
}