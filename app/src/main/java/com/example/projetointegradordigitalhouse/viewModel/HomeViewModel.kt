package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.*
import kotlinx.coroutines.launch
import com.example.projetointegradordigitalhouse.util.Constants
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository

internal class HomeViewModel(
    context: Context
) : ViewModel() {

    var homeCharList: MutableLiveData<List<CharacterResult>> = MutableLiveData()
    var homeSeriesList: MutableLiveData<List<SeriesResult>> = MutableLiveData()
    var homeComicsList: MutableLiveData<List<ComicResult>> = MutableLiveData()

    private val localDatabase: SearchDao by lazy { LocalDatabase.getDatabase(context).searchDao() }
    private val repository: MarvelXRepository by lazy {MarvelXRepository(context)}
    var lastSearchHistory: MutableLiveData<MutableList<String>> = MutableLiveData()

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
    fun getHomeCharacters() {
        viewModelScope.launch {
            val searchTagList = repository.getSearchTags()
            val favoriteCharactersList = repository.getFavoriteCharacters()
            val tempList = repository.getMostPopularCharacters(5)
            Log.i("HomeViewModel", "Character List com ${tempList.size} elementos")
            tempList.forEach {
                it.checkSearchTag(searchTagList)
                it.checkFavoriteTag(favoriteCharactersList)
            }
            homeCharList.postValue(tempList)
        }
    }
    fun getHomeSeries() {
        Log.i("HomeViewModel", "Series List")
        viewModelScope.launch {
            val searchTagList = repository.getSearchTags()
            val favoriteSeriesList = repository.getFavoriteSeries()
            val tempList = repository.getMostPopularSeries(5)
            tempList.forEach {
                it.checkSearchTag(searchTagList)
                it.checkFavoriteTag(favoriteSeriesList)
            }
            homeSeriesList.postValue(tempList)
        }
    }
    fun getHomeComics() {
        Log.i("HomeViewModel", "Comic List")
        viewModelScope.launch {
            val searchTagList = repository.getSearchTags()
            val favoriteComicList = repository.getFavoriteComics()
            val tempList = repository.getMostPopularComics(5)
            tempList.forEach {
                it.checkSearchTag(searchTagList)
                it.checkFavoriteTag(favoriteComicList)
            }
            homeComicsList.postValue(tempList)
        }
    }

    fun removeAllChips() {
        repository.removeAllChips()
    }
}