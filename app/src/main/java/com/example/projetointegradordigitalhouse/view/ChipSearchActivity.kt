package com.example.projetointegradordigitalhouse.view

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityChipSearchBinding
import com.example.projetointegradordigitalhouse.model.Search
import com.example.projetointegradordigitalhouse.model.characters.Result
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_DATA
import com.example.projetointegradordigitalhouse.view.adapter.ChipSearchAdapter
import com.example.projetointegradordigitalhouse.viewModel.ChipSearchViewModel
import com.example.projetointegradordigitalhouse.viewModel.HomeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDateTime
import java.util.*

class ChipSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChipSearchBinding
    private val viewModel by lazy {ChipSearchViewModel(this)}
    //private lateinit var viewModel: ChipSearchViewModel
    lateinit var searchTags: MutableSet<String?>
    private val characterList = mutableListOf<Result>()

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

        initComponents()
        setupObservables()
    }

    private fun initComponents() {
        //viewModel = ViewModelProvider(this).get(ChipSearchViewModel(this)::class.java)
        searchTags = mutableSetOf()
        binding.csBottomNavigation.menu.getItem(2).setChecked(true).setEnabled(false)
        binding.csTabLayout.addTab(binding.csTabLayout.newTab().setText("Characters"))
        binding.csTabLayout.addTab(binding.csTabLayout.newTab().setText("Series"))
        binding.csTabLayout.addTab(binding.csTabLayout.newTab().setText("Comics"))

        // Carregando histórico de busca feito no aplicativo
        viewModel.getSearchHistory()

    }
    private fun setupObservables() {
        binding.csSearchField.setEndIconOnClickListener {
            val newtag = binding.csSearchField.editText?.text.toString().trim()
            if (newtag!="") {
                viewModel.addSearchToLocalDatabase(Search(newtag,0, Date().toString()))
                if (searchTags.contains(newtag).not()) {
                    val chipDrawable = Chip(this)
                    chipDrawable.text = newtag
                    chipDrawable.isCloseIconVisible = true
                    chipDrawable.setOnCloseIconClickListener {
                        binding.csChipGroup.removeView(chipDrawable)
                        searchTags.remove(chipDrawable.text)
                    }
                    binding.csChipGroup.addView(chipDrawable)
                    searchTags.add(newtag)
                    binding.csSearchField.editText?.text?.clear()
                }
                viewModel.getCharactersByName(newtag)
            }
        }

        Log.i("ChipSearchActivity", "Configurando recycler view")
        viewModel.searchCharList.observe(this, {
            it?.let { charList ->
                binding.csRecyclerView.apply {
                    layoutManager = GridLayoutManager(this@ChipSearchActivity, 3)
                    adapter = ChipSearchAdapter(charList) { position ->
                        val intent = Intent(this@ChipSearchActivity, CharacterActivity::class.java)
                        intent.putExtra(KEY_INTENT_DATA, charList[position])
                        startActivity(intent)
                    }
                }
            }
        })
        viewModel.lastSearchHistory.observe(this, {
            it?.let { searchTags ->
                val adapter = ArrayAdapter(this@ChipSearchActivity, R.layout.list_item, searchTags)
                (binding.csSearchField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })

        binding.csTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                Toast.makeText(this@ChipSearchActivity, "Busca ${tab?.text}", Toast.LENGTH_LONG).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.csBottomNavigation.setOnNavigationItemSelectedListener(){
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

    private fun refreshResults() {
        TODO("Not yet implemented")
    }
}