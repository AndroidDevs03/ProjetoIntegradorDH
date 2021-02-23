package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.CharacterResult
import com.example.projetointegradordigitalhouse.model.ComicResult
import com.example.projetointegradordigitalhouse.model.SeriesResult
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(context: Context): ViewModel(){
    private val repository : MarvelXRepository by lazy { MarvelXRepository(context)}
    var charList: MutableLiveData<List<CharacterResult>> = MutableLiveData()
    var seriesList: MutableLiveData<List<SeriesResult>> = MutableLiveData()
    var comicsList: MutableLiveData<List<ComicResult>> = MutableLiveData()

    fun getCharacterFav() {
        var charFav = mutableListOf<CharacterResult>()

        Log.i("CharacterViewModel", "Series List")
        viewModelScope.launch {
            val allChars = repository.getAllChars()
            val favChars = repository.getFavoriteCharacters()


            allChars?.forEach {
                favChars?.forEach { charID ->
                    if (charID == it.id) {
                        charFav.add(it)
                    }
                }
            }
            charList.postValue(charFav.toList())
        }
    }

    fun getSeriesFav() {
        var seriesFav = mutableListOf<SeriesResult>()

        Log.i("CharacterViewModel", "Series List")
        viewModelScope.launch {
            val allSeries = repository.getAllSeries()
            val favSeries = repository.getFavoriteSeries()


            allSeries?.forEach {
                favSeries?.forEach { charID ->
                    if (charID == it.id) {
                        seriesFav.add(it)
                    }
                }
            }
            seriesList.postValue(seriesFav.toList())
        }
    }

    fun getComicsFav() {
        var comicsFav = mutableListOf<ComicResult>()

        Log.i("CharacterViewModel", "Series List")
        viewModelScope.launch {
            val allComics = repository.getAllComics()
            val favComics = repository.getFavoriteComics()


            allComics?.forEach {
                favComics?.forEach { charID ->
                    if (charID == it.id) {
                        comicsFav.add(it)
                    }
                }
            }
            comicsList.postValue(comicsFav.toList())
        }
    }
}

