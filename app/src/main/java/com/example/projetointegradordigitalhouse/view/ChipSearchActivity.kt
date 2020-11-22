package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityChipSearchBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout

class ChipSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChipSearchBinding
    lateinit var searchTags: MutableSet<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChipSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        setupObservables()
    }

    private fun initComponents() {
        searchTags = mutableSetOf()
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
            }
        }
        binding.csBottomNavigation.setOnNavigationItemSelectedListener(){
            when(it.itemId){
                R.id.page_1 -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.page_2 -> {
                    //startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }
                R.id.page_3 -> {
                    //startActivity(Intent(this, ChipSearchActivity::class.java))
                    true
                }
                R.id.page_4 -> {
                    //startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                else -> {false}
            }
        }
    }
}