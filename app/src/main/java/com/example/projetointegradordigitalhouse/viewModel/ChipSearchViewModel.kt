package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.util.Constants.Values.CONST_MAX_SEARCH_HISTORY
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ChipSearchViewModel(
    context: Context
) : ViewModel() {
    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }

    var searchResultList: MutableLiveData<Pair<MutableList<CharacterResult>, MutableList<SeriesResult>>> = MutableLiveData()
    var searchSeriesList: MutableLiveData<MutableList<SeriesResult>> = MutableLiveData()
    var lastSearchHistory: MutableLiveData<MutableList<String>> = MutableLiveData()
    var tagList: MutableLiveData<MutableSet<String>> = MutableLiveData()

    fun searchByName(name: String) {
        viewModelScope.launch {
            val searchTagList = repository.getSearchTags()
            val favoriteCharactersList = repository.getFavoriteCharacters()
            val favoriteSeriesList = repository.getFavoriteSeries()
            val tempResult = repository.searchByName(name, 10, 0)
            tempResult.first.forEach {
                it.checkSearchTag(searchTagList)
                it.checkFavoriteTag(favoriteCharactersList)
            }
            tempResult.second.forEach {
                it.checkSearchTag(searchTagList)
                it.checkFavoriteTag(favoriteSeriesList)
            }
            searchResultList.postValue(tempResult)
        }
    }
    fun updateSeriesByCharacterId(charId: Long) {
        viewModelScope.launch { repository.updateSeriesByCharacterID(charId) }
    }
    fun updateComicsBySeriesId(seriesId: Long) {
        viewModelScope.launch { repository.updateComicsBySeriesID(seriesId) }
    }
    fun getSeriesByName(name: String, limit: Int = 10, offset: Int = 0) {
        viewModelScope.launch { searchSeriesList.postValue(repository.getSeriesByName(name, limit, offset)) }
    }
    fun getSearchHistory() {
        viewModelScope.launch { lastSearchHistory.postValue(repository.getSearchHistory()) }
    }
    fun addSearchToLocalDatabase(tag: String) {
        viewModelScope.launch {
            repository.insertSearchHistory(tag)
            val tempNewList: MutableList<String> = lastSearchHistory.value ?: mutableListOf()
            //Se já tiver alguma busca recente com a mesma tag, ele joga ela em primeiro
            if (tag in tempNewList) {
                tempNewList.remove(tag)
                tempNewList.add(0, tag)
            } else if (tempNewList.size >= CONST_MAX_SEARCH_HISTORY) {
                tempNewList.add(0, tag)
                tempNewList.removeLast()
            } else {
                tempNewList.add(0, tag)
            }
            lastSearchHistory.postValue(tempNewList)
        }
    }
    fun getSearchTags(){
        viewModelScope.launch { tagList.postValue(repository.getSearchTags())}
    }
    fun addSearchTag(tag: String, tabPosition: Int) {
        viewModelScope.launch {repository.addSearchTags(tag, tabPosition)}
        getSearchTags()
    }
    fun removeSearchTag(tag: String, tabPosition: Int) {
        viewModelScope.launch {repository.removeSearchTags(tag, tabPosition)}
        getSearchTags()
    }
    fun addFavorite(result: Any, tabPosition: Int) {
        viewModelScope.launch { repository.addToFavorites(result, tabPosition) }
    }
    fun remFavorite(result: Any, tabPosition: Int) {
        viewModelScope.launch { repository.removeFromFavorites(result, tabPosition) }
    }
}