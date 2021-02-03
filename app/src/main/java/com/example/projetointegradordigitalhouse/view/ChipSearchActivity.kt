package com.example.projetointegradordigitalhouse.view

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityChipSearchBinding
import com.example.projetointegradordigitalhouse.model.characters.Result
import com.example.projetointegradordigitalhouse.util.Constants.Intent.KEY_INTENT_DATA
import com.example.projetointegradordigitalhouse.view.adapter.ChipSearchAdapter
import com.example.projetointegradordigitalhouse.viewModel.ChipSearchViewModel
import com.example.projetointegradordigitalhouse.viewModel.HomeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout

class ChipSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChipSearchBinding
    private lateinit var viewModel: ChipSearchViewModel
    lateinit var searchTags: MutableSet<String?>
    private val characterList = mutableListOf<Result>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChipSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        setupObservables()
    }



    private fun initComponents() {
        viewModel = ViewModelProvider(this).get(ChipSearchViewModel::class.java)
        searchTags = mutableSetOf()
        binding.csBottomNavigation.menu.getItem(2).setChecked(true).setEnabled(false)
        binding.csTabLayout.addTab(binding.csTabLayout.newTab().setText("Characters"))
        binding.csTabLayout.addTab(binding.csTabLayout.newTab().setText("Series"))
        binding.csTabLayout.addTab(binding.csTabLayout.newTab().setText("Comics"))

    }
    private fun setupObservables() {
        binding.csSearchField.setEndIconOnClickListener {
            val newtag = binding.csSearchField.editText?.text.toString().trim()
            if (newtag!="") {
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
                viewModel.searchCharList.observe(this, {
                    characterList.addAll(it)
                    refreshResults()
                })
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