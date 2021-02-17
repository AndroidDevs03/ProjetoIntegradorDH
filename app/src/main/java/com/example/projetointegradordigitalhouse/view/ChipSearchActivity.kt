package com.example.projetointegradordigitalhouse.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityChipSearchBinding
import com.example.projetointegradordigitalhouse.model.Avatar
import com.example.projetointegradordigitalhouse.model.CharacterResult
import com.example.projetointegradordigitalhouse.model.GeneralResult
import com.example.projetointegradordigitalhouse.model.SeriesResult
import com.example.projetointegradordigitalhouse.util.Constants
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_DATA
import com.example.projetointegradordigitalhouse.view.adapter.ChipSearchAdapter
import com.example.projetointegradordigitalhouse.viewModel.ChipSearchViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ChipSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChipSearchBinding
    private val viewModel by lazy { ChipSearchViewModel(this) }
    private val incomingSearch: String? by lazy { intent.getStringExtra(Constants.Intent.KEY_INTENT_SEARCH) }
    lateinit var searchTags: MutableSet<String?>
    var tabPosition: Int = 0
    private var specialSearchActivated: Boolean = false
    private lateinit var activeSearch: String
    private val firebaseAuth by lazy{ Firebase.auth }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView : NavigationView

    private val firebaseFirestore by lazy {
        Firebase.firestore
    }

    // Ao preencher o campo de busca, consultar o banco de dados do Firebase pelas buscas já feitas e colocá-las na hint
    // Ao confirmar a busca, acrescentar a tag no banco de dados local e consultar o banco de dados "searchtags"
    // No BD "searchtags", caso a tag já tenha sido utilizada, verificar a data de última busca.
    //      caso a última busca tenha sido feita há mais de um dia, atualizar os resultados buscando novamente da api. Se ela não retornar resultados, não registrá-la no Firebase
    //      caso não, buscar os resultados do Firebase.
    // Criar as três listas de resultados: characters, series e comics. Atualizar o TabLayout com a quantidade de resultados
    // Atualizar a recycler de acordo com a tab


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChipSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.dlPerfil
        navigationView = binding.nvPerfil

        initComponents()
        setupObservables()
    }

    private fun initComponents() {
        incomingSearch?.let {
            executeSearch(it)
        } ?: run {
            activeSearch = ""
        }
        binding.csSearchField.editText?.setText(activeSearch)
        searchTags = mutableSetOf()
        binding.csBottomNavigation.menu.getItem(2).setChecked(true).setEnabled(false)
        binding.csTabLayout.addTab(binding.csTabLayout.newTab().setText("Characters"))
        binding.csTabLayout.addTab(binding.csTabLayout.newTab().setText("Series"))

        // Carregando histórico de busca feito no aplicativo
        viewModel.getSearchHistory()

    }

    @SuppressLint("SetTextI18n")
    private fun setupObservables() {
        viewModel.lastSearchHistory.observe(this, {
            it?.let { searchTags ->
                val adapter = ArrayAdapter(this@ChipSearchActivity, R.layout.list_item, searchTags)
                (binding.csSearchField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })
        binding.csAutoComplete.setOnItemClickListener { _, view, position, id ->
            executeSearch(binding.csSearchField.editText?.text.toString().trim())
        }
        binding.csSearchField.setEndIconOnClickListener {
            executeSearch(binding.csSearchField.editText?.text.toString().trim())
        }
        binding.csSearchField.setEndIconOnLongClickListener(View.OnLongClickListener {
            changeSearchType()
        })

        Log.i("ChipSearchActivity", "Configurando recycler view")
        viewModel.searchResultList.observe(this, {
            it?.let { charList ->
                updateRecyclerView(tabPosition, charList)
            }
        })
        viewModel.lastSearchHistory.observe(this, {
            it?.let { searchTags ->
                val adapter = ArrayAdapter(this@ChipSearchActivity, R.layout.list_item, searchTags)
                (binding.csSearchField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })

        binding.csTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab?.position ?: 0
                viewModel.searchResultList.value?.let { updateRecyclerView(tabPosition, it) }
                Toast.makeText(this@ChipSearchActivity, "Busca ${tab?.text}", Toast.LENGTH_LONG)
                    .show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.csBottomNavigation.setOnNavigationItemSelectedListener {
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
                    it.isEnabled = false
//                    startActivity(Intent(this, ChipSearchActivity::class.java))
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

    private fun executeSearch(search: String) {
        activeSearch = search
        if (search != "") {
            viewModel.addSearchToLocalDatabase(search)
            if (specialSearchActivated && searchTags.contains(search).not()) {
                val chipDrawable = Chip(this)
                chipDrawable.text = search
                chipDrawable.isCloseIconVisible = true
                chipDrawable.setOnCloseIconClickListener {
                    binding.csChipGroup.removeView(chipDrawable)
                    searchTags.remove(chipDrawable.text)
                }
                binding.csChipGroup.addView(chipDrawable)
                searchTags.add(search)
                binding.csSearchField.editText?.text?.clear()
            }
            viewModel.searchByName(search)
        }
    }

    private fun updateRecyclerView(
        tab: Int,
        resultLists: Pair<MutableList<CharacterResult>, MutableList<SeriesResult>>
    ) {
        if (tab == 0) {
            val charList = resultLists.first

            binding.csRecyclerView.apply {
                layoutManager = GridLayoutManager(this@ChipSearchActivity, 1)
                adapter = ChipSearchAdapter(
                    viewModel,
                    charList as MutableList<GeneralResult>,
                    0,
                    { position ->
                        val intent = Intent(this@ChipSearchActivity, CharacterActivity::class.java)
                        intent.putExtra(KEY_INTENT_DATA, charList[position])
                        startActivity(intent)
                    },
                    { add, position ->
                        if (add){
                            viewModel.addFavorite(charList[position],0)
                        }
                        else {
                            viewModel.remFavorite(charList[position],0)
                        }
                    })
            }
        } else {
            val serieslist = resultLists.second
            binding.csRecyclerView.apply {
                layoutManager = GridLayoutManager(this@ChipSearchActivity, 1)
                adapter = ChipSearchAdapter(
                    viewModel,
                    serieslist as MutableList<GeneralResult>,
                    1,
                { position ->
                    val intent = Intent(this@ChipSearchActivity, SeriesActivity::class.java)
                    intent.putExtra(KEY_INTENT_DATA, serieslist[position])
                    startActivity(intent)
                },
                    { add, position ->
                        if (add){
                            viewModel.addFavorite(serieslist[position],1)
                        }
                        else {
                            viewModel.remFavorite(serieslist[position],1)
                        }
                    })
            }
        }
    }

    private fun refreshResults() {
        TODO("Not yet implemented")
    }

    fun changeSearchType(): Boolean {
        if (specialSearchActivated) {
            binding.csSearchField.setEndIconDrawable(R.drawable.ic_baseline_search_24)
            specialSearchActivated = false
        } else {
            binding.csSearchField.setEndIconDrawable(R.drawable.ic_baseline_search_off_24)
            specialSearchActivated = true
        }
        return true
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