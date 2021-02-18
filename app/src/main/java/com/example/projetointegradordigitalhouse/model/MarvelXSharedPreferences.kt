package com.example.projetointegradordigitalhouse.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.projetointegradordigitalhouse.util.Constants.SharedPreferences.NAME_SP_CURRENT_SEARCH
import com.example.projetointegradordigitalhouse.util.Constants.SharedPreferences.NAME_SP_DBNAME

class MarvelXSharedPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences by lazy { context.getSharedPreferences(NAME_SP_DBNAME, MODE_PRIVATE) }

    fun updateTags(searchTags: MutableSet<String>) {
        searchTags.removeAll{it == ""}
        sharedPreferences.edit {putStringSet(NAME_SP_CURRENT_SEARCH, searchTags)}

    }
    fun getTags(): MutableSet<String>? {
        val tempTags = sharedPreferences.getStringSet(NAME_SP_CURRENT_SEARCH, mutableSetOf(""))
        tempTags?.removeAll{it == ""}
        return tempTags
    }
}