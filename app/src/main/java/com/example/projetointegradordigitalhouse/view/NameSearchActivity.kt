package com.example.projetointegradordigitalhouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityNameSearchBinding
import com.example.projetointegradordigitalhouse.model.characters.Result
import com.example.projetointegradordigitalhouse.util.Constants.Api.KEY_ACTIVE_SEARCH
import com.example.projetointegradordigitalhouse.util.Constants.Api.KEY_INTENT_SEARCH
import com.example.projetointegradordigitalhouse.view.adapter.NameSearchAdapter
import com.example.projetointegradordigitalhouse.viewModel.NameSearchViewModel

class NameSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNameSearchBinding
    private lateinit var viewModel: NameSearchViewModel
    private var characterList = mutableListOf<Result>()
    private val incomingSearch: String? by lazy {intent.getStringExtra(KEY_INTENT_SEARCH)}
    private lateinit var activeSearch: String

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
                viewModel.getCharactersByName(activeSearch)
            }
        }
        binding.nsBottomNavigation.setOnNavigationItemSelectedListener(){
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
        viewModel.getCharactersByName(activeSearch)
    }
}