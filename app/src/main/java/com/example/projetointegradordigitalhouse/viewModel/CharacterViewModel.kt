package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.util.Constants
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import kotlinx.coroutines.launch

class CharacterViewModel(context: Context) : ViewModel() {

    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }

    var charSeriesList: MutableLiveData<List<SeriesResult>> = MutableLiveData()
    var charComicsList: MutableLiveData<List<ComicResult>> = MutableLiveData()
    var lastSearchHistory: MutableLiveData<MutableList<String>> = MutableLiveData()
    private val localDatabase: SearchDao by lazy { LocalDatabase.getDatabase(context).searchDao() }

    fun getSearchHistory() {
        viewModelScope.launch {
            lastSearchHistory.postValue(localDatabase.getLastSearchResults() as MutableList<String>)
        }
    }
    fun addSearchToLocalDatabase(search: Search){
        viewModelScope.launch {
            val tempNewList: MutableList<String> = lastSearchHistory.value ?: mutableListOf()
            localDatabase.insert(search)
            //Se já tiver alguma busca recente com a mesma tag, ele joga ela em primeiro
            if (search.busca in tempNewList) {
                tempNewList.remove(search.busca)
                tempNewList.add(0, search.busca)
            } else if (tempNewList.size >= Constants.Values.CONST_MAX_SEARCH_HISTORY){
                tempNewList.add(0, search.busca)
                tempNewList.removeLast()
            } else {
                tempNewList.add(0, search.busca)
            }
            lastSearchHistory.postValue(tempNewList)
        }
    }

    fun getCharacterSeries(charSeriesID: List<Long>?) {
        var charSeries = mutableListOf<SeriesResult>()

        Log.i("CharacterViewModel", "Series List")
        viewModelScope.launch {
            val allseries = repository.getAllSeries()

            allseries?.forEach {
                charSeriesID?.forEach { serieID ->
                    if (serieID == it.id) {
                        charSeries.add(it)
//                        repository.updateComicsBySeriesID(serieID)
//                        Log.i("CharacterViewModel", " ADD: ${it.id} ${it.name} Series Total - ${charSeries.size}")
                    }
                }
            }
            charSeriesList.postValue(charSeries.toList())
        }
    }

    fun getCharacterComics(charID: Long) {
        var charComics = mutableListOf<ComicResult>()

        Log.i("CharacterViewModel", "Comic List")
        viewModelScope.launch {
            val allcomics = repository.getAllComics()

            allcomics?.forEach { comic ->
                comic.charactersList?.let { listCharsID ->
                    listCharsID.forEach {
                        if (it == charID) {
                            charComics.add(comic)
//                            Log.i("CharacterViewModel", " ADD: ${comic.id} ${comic.name} Comics Total - ${charComics.size}")

                        }

                    }
                }
            }
            charComicsList.postValue(charComics.toList())
        }
    }

    fun addFavorite(result: Any, tabPosition: Int) {
        viewModelScope.launch { repository.addToFavorites(result, tabPosition) }
    }

    fun remFavorite(result: Any, tabPosition: Int) {
        viewModelScope.launch { repository.removeFromFavorites(result, tabPosition) }
    }

    fun addSearchTag(tag: String, tabPosition: Int) {
        viewModelScope.launch {repository.addSearchTags(tag, tabPosition)}
    }


    fun removeSearchTag(tag: String, tabPosition: Int) {
        viewModelScope.launch { repository.removeSearchTags(tag, tabPosition) }
    }
}