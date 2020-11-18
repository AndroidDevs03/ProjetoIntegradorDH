package com.example.projetointegradordigitalhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout

class ChipSearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chip_search)

        val searchField = findViewById<TextInputLayout>(R.id.cs_searchField)
        val chipSearch = findViewById<ChipGroup>(R.id.cs_chipGroup)

        val searchTags = mutableSetOf<String?>()

        searchField.setEndIconOnClickListener {
            val newtag = searchField.editText?.text.toString()
            if (searchTags.contains(newtag).not()){
                val chipDrawable = Chip(this)
                chipDrawable.text = newtag
                chipDrawable.isCloseIconVisible = true
                chipDrawable.setOnCloseIconClickListener {
                    chipSearch.removeView(chipDrawable)
                    searchTags.remove(chipDrawable.text)}
                chipSearch.addView(chipDrawable)
                searchTags.add(newtag)
            }
        }
    }
}