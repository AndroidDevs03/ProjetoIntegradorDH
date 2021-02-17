package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.CharacterResult
import com.example.projetointegradordigitalhouse.model.SeriesResult
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import kotlinx.coroutines.launch

class ComicViewModel(context: Context): ViewModel() {

    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }

    var comicCharList: MutableLiveData<List<CharacterResult>> = MutableLiveData()

    fun getComicCharacters(charactersListID: List<Long>) {
        var  comicChars = mutableListOf<CharacterResult>()

        Log.i("ComicrViewModel", "Character List")
        viewModelScope.launch {
            val allCharacters = repository.getAllChars()

            allCharacters?.forEach {
                charactersListID?.forEach { serieID ->
                    if(serieID == it.id){
                        comicChars.add(it)
//                        Log.i("ComicViewModel", " ADD: ${it.id} ${it.name} Chars Total - ${comicChars.size}")
                    }
                }
            }
            comicCharList.postValue(comicChars.toList())
        }
    }


}