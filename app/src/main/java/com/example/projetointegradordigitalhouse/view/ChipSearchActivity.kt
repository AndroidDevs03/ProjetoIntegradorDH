package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityChipSearchBinding
import com.example.projetointegradordigitalhouse.model.CharacterResult
import com.example.projetointegradordigitalhouse.model.GeneralResult
import com.example.projetointegradordigitalhouse.model.SeriesResult
import com.example.projetointegradordigitalhouse.util.Constants
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_DATA
import com.example.projetointegradordigitalhouse.util.Constants.SharedPreferences.PREFIX_CHAR
import com.example.projetointegradordigitalhouse.util.Constants.SharedPreferences.PREFIX_COMIC
import com.example.projetointegradordigitalhouse.util.Constants.SharedPreferences.PREFIX_SERIES
import com.example.projetointegradordigitalhouse.view.adapter.ChipSearchAdapter
import com.example.projetointegradordigitalhouse.viewModel.ChipSearchViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import java.util.*

class ChipSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChipSearchBinding
    private val viewModel by lazy { ChipSearchViewModel(this) }
    private val incomingSearch: String? by lazy { intent.getStringExtra(Constants.Intent.KEY_INTENT_SEARCH) }
    var searchTags: MutableSet<String> = mutableSetOf()
    var tabPosition: Int = 0
    private var specialSearchActivated: Boolean = false
    private lateinit var activeSearch: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChipSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        loadContent()
        setupObservables()
        setupListeners()
    }
    override fun onResume() {
        super.onResume()
        updateChipList()
    }
    private fun setupListeners() {
        binding.csAutoComplete.setOnItemClickListener { _, view, position, id ->
            executeSearch(binding.csSearchField.editText?.text.toString().trim())
        }
        binding.csSearchField.setEndIconOnClickListener {
            executeSearch(binding.csSearchField.editText?.text.toString().trim())
        }
        binding.csSearchField.editText?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                executeSearch(binding.csSearchField.editText?.text.toString().trim())
            }
            false
        }
        binding.csSearchField.setEndIconOnLongClickListener(View.OnLongClickListener {
            viewModel.addSearchTag("0_${binding.csSearchField.editText?.text.toString().trim()}",4)
            true
        })
        binding.csTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab?.position ?: 0
                viewModel.searchResultList.value?.let { updateRecyclerView(tabPosition, it) }
                //Toast.makeText(this@ChipSearchActivity, "${tab?.text} Search", Toast.LENGTH_LONG).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        binding.csBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    //drawerLayout.open()
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
    }
    private fun loadContent() {
        // Carregando histórico de busca feito no aplicativo
        viewModel.getSearchHistory()
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

    }
    private fun setupObservables() {
        viewModel.lastSearchHistory.observe(this, {
            it?.let { searchTags ->
                val adapter = ArrayAdapter(this@ChipSearchActivity, R.layout.list_item, searchTags)
                (binding.csSearchField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })
        viewModel.tagList.observe(this,{
            it?.let{
                if (it.size > 0) {
                    specialSearchActivated = true
                    searchTags = it
                    updateChipList()
                } else {
                    specialSearchActivated = false
                    searchTags.removeAll { true }
                    updateChipList()
                }
            }
        })
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
    }
    private fun executeSearch(search: String) {
        activeSearch = search
        if (search != "") {
            viewModel.addSearchToLocalDatabase(search)
            viewModel.searchByName(search)
        }
    }
    private fun updateChipList() {
        binding.csChipGroup.removeAllViews()
        if (specialSearchActivated) {
            searchTags.forEach { search ->
                val chipInfo = search.split("_")
                if (chipInfo.size == 3) {
                    val tab = when (chipInfo[0]) {
                        PREFIX_CHAR -> 0
                        PREFIX_SERIES -> 1
                        PREFIX_COMIC -> 2
                        else -> 4
                    }
                    val chipDrawable = Chip(this)
                    chipDrawable.text = chipInfo[2].take(11)
                    chipDrawable.isCloseIconVisible = true
                    if (tab == 0) {
                        chipDrawable.chipBackgroundColor = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this,
                                R.color.characterChipBackground
                            )
                        )
                    } else if (tab == 1) {
                        chipDrawable.chipBackgroundColor = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this,
                                R.color.seriesChipBackground
                            )
                        )
                    } else if (tab == 4) {
                        chipDrawable.chipBackgroundColor = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this,
                                R.color.descriptionChipBackground
                            )
                        )
                    }
                    chipDrawable.setOnCloseIconClickListener {
                        binding.csChipGroup.removeView(chipDrawable)
                        viewModel.removeSearchTag("${chipInfo[1]}_${chipInfo[2]}", tab)
                    }
                    binding.csChipGroup.addView(chipDrawable)
                    binding.csSearchField.editText?.text?.clear()
                }
            }
            viewModel.filter(searchTags)
        }
    }
    private fun updateRecyclerView(tab: Int, resultLists: Pair<MutableList<CharacterResult>, MutableList<SeriesResult>>) {
        if (tab == 0) {
            val charList = resultLists.first

            binding.csRecyclerView.apply {
                layoutManager = GridLayoutManager(this@ChipSearchActivity, 1)
                adapter = ChipSearchAdapter(
                    viewModel,
                    charList as MutableList<GeneralResult>,
                    // função para entrar na página
                    { position ->
                        val intent = Intent(this@ChipSearchActivity, CharacterActivity::class.java)
                        intent.putExtra(KEY_INTENT_DATA, charList[position])
                        startActivity(intent)
                    },
                    // função para botão de busca
                    { add, position ->
                        if (add){ viewModel.addSearchTag("${charList[position].id}_${charList[position].name}",0) }
                        else { viewModel.removeSearchTag("${charList[position].id}_${charList[position].name}",0) }
                    },
                    // função para botão de favoritos
                    { add, position ->
                        if (add){ viewModel.addFavorite(charList[position],0) }
                        else { viewModel.remFavorite(charList[position],0) }
                    })
            }
        } else {
            val serieslist = resultLists.second
            binding.csRecyclerView.apply {
                layoutManager = GridLayoutManager(this@ChipSearchActivity, 1)
                adapter = ChipSearchAdapter(
                    viewModel,
                    serieslist as MutableList<GeneralResult>,
                    // função para entrar na página
                    { position ->
                        val intent = Intent(this@ChipSearchActivity, SeriesActivity::class.java)
                        intent.putExtra(KEY_INTENT_DATA, serieslist[position])
                        startActivity(intent)
                    },
                    // função para botão de busca
                    { add, position ->
                        if (add){ viewModel.addSearchTag("${serieslist[position].id}_${serieslist[position].name}",1) }
                        else { viewModel.removeSearchTag("${serieslist[position].id}_${serieslist[position].name}",1) }
                    },
                    // função para botão de favoritos
                    { add, position ->
                        if (add){ viewModel.addFavorite(serieslist[position],1) }
                        else { viewModel.remFavorite(serieslist[position],1) }
                    })
            }
        }
    }
    private fun refreshResults() {
        TODO("Not yet implemented")
    }
    fun changeSearchMode(): Boolean {
        if (specialSearchActivated) {
            binding.csSearchField.setEndIconDrawable(R.drawable.ic_baseline_search_24)
            specialSearchActivated = false
        } else {
            binding.csSearchField.setEndIconDrawable(R.drawable.ic_baseline_search_off_24)
            specialSearchActivated = true
        }
        updateChipList()
        return true
    }
}