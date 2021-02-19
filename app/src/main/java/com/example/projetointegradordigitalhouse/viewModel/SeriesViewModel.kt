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

class SeriesViewModel(context: Context):ViewModel() {


    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }

    var seriesCharsList: MutableLiveData<List<CharacterResult>> = MutableLiveData()
    var seriesComicsList: MutableLiveData<List<ComicResult>> = MutableLiveData()
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



    fun getSeriesCharacters(charactersListID: List<Long>) {
        var  serieChars = mutableListOf<CharacterResult>()

        Log.i("SeriesViewModel", "Character List")
        viewModelScope.launch {
            val allCharacters = repository.getAllChars()

            allCharacters?.forEach {
                charactersListID?.forEach { charID ->
                    if(charID == it.id){
                        serieChars.add(it)
//                        Log.i("SeriesViewModel", " ADD: ${it.id} ${it.name} Chars Total - ${serieChars.size}")
                    }
                }
            }
            seriesCharsList.postValue(serieChars.toList())
        }
    }

    fun getSeriesComics(serieID: Long){
        var  seriesComics = mutableListOf<ComicResult>()

        Log.i("SeriesViewModel", "Comic List")
        viewModelScope.launch {
            seriesComics = repository.getComicsBySerieID(serieID)
            seriesComicsList.postValue(seriesComics.toList())
        }
        Log.i("SeriesViewModel", " Comics Total - ${seriesComics.size}")

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