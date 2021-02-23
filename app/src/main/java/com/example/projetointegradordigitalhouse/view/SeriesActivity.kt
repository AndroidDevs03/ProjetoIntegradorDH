package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivitySeriesBinding
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.util.Constants
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_SERIES
import com.example.projetointegradordigitalhouse.viewModel.SeriesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.synnapps.carouselview.CarouselView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_series.*
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.*


class SeriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeriesBinding
    private var series : SeriesResult? = null
//    private var serieComics: List<Long>? = null
//    private var serieChars: List<Long>? = null
    private val firebaseAuth by lazy{ Firebase.auth }
    private val viewModel by lazy { SeriesViewModel(this) }

    private var comicsList = mutableListOf<ComicResult>()
    private var charsList = mutableListOf<CharacterResult>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val firebaseFirestore by lazy {
        Firebase.firestore
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        series = intent.getParcelableExtra(KEY_INTENT_SERIES)

        drawerLayout = binding.dlPerfil
        navigationView = binding.nvPerfil

        initComponents()
        setupObservables()
    }

    private fun setupObservables() {
        viewModel.lastSearchHistory.observe(this, {
            it?.let { searchTags ->
                val adapter = ArrayAdapter(this@SeriesActivity, R.layout.list_item, searchTags)
                (binding.sdSearchField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })
        binding.sdSearchField.setEndIconOnClickListener {
            val newtag = binding.sdSearchField.editText?.text.toString().trim()
            if (newtag != "") {
                viewModel.addSearchToLocalDatabase(
                    Search(
                        newtag,
                        "",
                        Date().toString()
                    )
                )
                val intent = Intent(this@SeriesActivity, ChipSearchActivity::class.java)
                intent.putExtra(Constants.Intent.KEY_INTENT_SEARCH, newtag)
                startActivity(intent)
            }
        }
    }

    private fun initComponents() {
        viewModel.getSearchHistory()

//        firebaseAuth?.let{ auth ->
            series?.let{ seriesResult ->

                seriesResult.charactersList?.let{
                    viewModel.getSeriesCharacters(it)
                }
                seriesResult.id?.let{
                    viewModel.getSeriesComics(it)
                    val check = viewModel.seriesComicsList

                }
                seriesResult.name?.let{
                    binding.tvSeriesTitle.text = it
                }
                seriesResult.description?.let{
                    binding.tvSeriesDescription.text = it
                }
                seriesResult.thumbnail?.let{
                    Glide.with(this).load(it).into(binding.ivSeriesPicture)
                }
                //Configurando botão de Favoritar
                if (firebaseAuth.currentUser?.isAnonymous?.not() == true){
                    Log.i("RecyclerView", "Usuário identificado")
                    binding.ibSeriesFavorite.isSelected = seriesResult.favoriteTagFlag
                    binding.ibSeriesFavorite.setOnClickListener {
                        if (binding.ibSeriesFavorite.isSelected){
                            favoriteClicked(false) // true = adicionar, false = remover
                            binding.ibSeriesFavorite.isSelected = false
                        } else {
                            favoriteClicked(true) // true = adicionar, false = remover
                            binding.ibSeriesFavorite.isSelected = true
                        }
                    }
                    binding.ibSeriesFavorite.isActivated = true
                }else{
                    Log.i("RecyclerView", "Usuário anônimo")
                    binding.ibSeriesFavorite.isActivated = false
                    binding.ibSeriesFavorite.setOnClickListener {
                        Toast.makeText(
                            binding.ibSeriesFavorite.context,
                            "Favorite is not allowed for unregistered users. Please sign in.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                //Configurando botão de chipSearch
                binding.ibSeriesSearch.isSelected = seriesResult.searchTagFlag
                binding.ibSeriesSearch.setOnClickListener {
                    if (binding.ibSeriesSearch.isSelected){
                        searchClicked(false) // true = adicionar, false = remover
                        binding.ibSeriesSearch.isSelected = false
                    } else {
                        searchClicked(true) // true = adicionar, false = remover
                        binding.ibSeriesSearch.isSelected = true
                    }
                }
                initCharacters()
                initComics()
            }
//        }?: run{
//            startActivity(Intent(this, LoginActivity::class.java))
//        }

 //        findViewById<ImageButton>(R.id.ibSeriesSearch).setOnClickListener {
//            startActivity(Intent(this,ChipSearchActivity::class.java))
//        }
//
//        findViewById<ImageButton>(R.id.ibSeriesFavoriteNO).setOnClickListener {
//            findViewById<ImageButton>(R.id.ibSeriesFavoriteYes).visibility = View.VISIBLE
//            findViewById<ImageButton>(R.id.ibSeriesFavoriteNO).visibility = View.INVISIBLE
//        }
//        findViewById<ImageButton>(R.id.ibSeriesFavoriteYes).setOnClickListener {
//            findViewById<ImageButton>(R.id.ibSeriesFavoriteNO).visibility = View.VISIBLE
//            findViewById<ImageButton>(R.id.ibSeriesFavoriteYes).visibility = View.INVISIBLE
//        }

        findViewById<ImageButton>(R.id.ibSeriesShare).setOnClickListener {
            share()
        }

        findViewById<CarouselView>(R.id.cvSeriesCharacters).setImageClickListener {
            val intent = Intent(this, CharacterActivity::class.java)
            val temp = charsList[it]
            intent.putExtra(Constants.Intent.KEY_INTENT_CHARACTER, temp)
            startActivity(intent)
        }

        findViewById<CarouselView>(R.id.cvSeriesComics).setImageClickListener {
            val intent = Intent(this, ComicActivity::class.java)
            val temp = comicsList[it]
            intent.putExtra(Constants.Intent.KEY_INTENT_COMIC, temp)
            startActivity(intent)
        }

        findViewById<BottomNavigationView>(R.id.bnvSeries).setOnNavigationItemSelectedListener {
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

    private fun initComics() {
        viewModel.seriesComicsList.observe(
            this, {
                comicsList.addAll(it)
                Log.i("CarouselView", "${comicsList.size} Comics")
                binding.cvSeriesComics.setImageListener(
                    CarouselListener(
                        this,
                        comicsList as MutableList<GeneralResult>
                    )
                )
                binding.cvSeriesComics.pageCount = comicsList.size

            }
        )
    }
    private fun favoriteClicked(add: Boolean) {
        if (add){ series?.let { viewModel.addFavorite(it, 1) } }
        else { series?.let { viewModel.remFavorite(it, 1) } }
    }
    private fun searchClicked(add: Boolean) {
        if (add){ series?.let { viewModel.addSearchTag("${it.id}_${it.name}", 1) }}
        else { series?.let {viewModel.removeSearchTag("${it.id}_${it.name}", 1) }}
    }

    private fun initCharacters() {
        viewModel.seriesCharsList.observe(
            this, {
                charsList.addAll(it)
                Log.i("CarouselView", "${charsList.size} Characters")
                binding.cvSeriesCharacters.setImageListener(
                    CarouselListener(
                        this,
                        charsList as MutableList<GeneralResult>
                    )
                )
                binding.cvSeriesCharacters.pageCount = charsList.size

            }
        )
    }

    private fun share(){
        val intent : Intent = Intent(Intent.ACTION_SEND)
        intent.setType("image/png")
        val drawable : BitmapDrawable = ivSeriesPicture.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val bytes : ByteArrayOutputStream = ByteArrayOutputStream()
        val description : String = findViewById<TextView>(R.id.tvSeriesDescription).text.toString()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path : String = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "Favorite Character",
            null
        )
        val uri : Uri = Uri.parse(path)
        intent.putExtra(Intent.EXTRA_COMPONENT_NAME, "Introducing content previews")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_TEXT, description)
        startActivity(Intent.createChooser(intent, "Compartilhar nas redes sociais"))
    }
}