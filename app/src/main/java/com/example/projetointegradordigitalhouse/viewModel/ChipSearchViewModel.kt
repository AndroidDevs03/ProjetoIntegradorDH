package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.util.Constants.Values.CONST_MAX_SEARCH_RESULTS
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.LocalDate.now

class ChipSearchViewModel(
    context: Context
) : ViewModel() {
    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }
    private val localDatabase: SearchDao by lazy { LocalDatabase.getDatabase(context).searchDao() }
    private val sharedPreferences: MarvelXSharedPreferences by lazy {
        MarvelXSharedPreferences(
            context
        )
    }
    private val firebaseAuth by lazy { Firebase.auth }


    var searchCharList: MutableLiveData<MutableList<CharacterResult>> = MutableLiveData()
    var searchSeriesList: MutableLiveData<MutableList<SeriesResult>> = MutableLiveData()
    var searchComicList: MutableLiveData<MutableList<ComicResult>> = MutableLiveData()
    var lastSearchHistory: MutableLiveData<MutableList<String>> = MutableLiveData()

    fun getCharactersByName(name: String, limit: Int = 10, offset: Int = 0) {
        viewModelScope.launch {
            searchCharList.postValue(repository.getCharactersByName(name, limit, offset))
        }
    }
    fun updateSeriesByCharacterId(charId: Int) {
        viewModelScope.launch {
            repository.updateSeriesByCharacterID(charId)
        }
    }
    fun updateComicsBySeriesId(seriesId: Int) {
        viewModelScope.launch {
            repository.updateComicsBySeriesID(seriesId)
        }
    }
    fun getSeriesByName(name: String, limit: Int = 10, offset: Int = 0) {
        viewModelScope.launch {
            searchSeriesList.postValue(repository.getSeriesByName(name, limit, offset))
        }
    }

    fun getComicsByName(name: String, limit: Int = 10, offset: Int = 0) {
        viewModelScope.launch {
            searchComicList.postValue(repository.getComicsByName(name, limit, offset))
        }
    }

    fun getSearchHistory() {
        viewModelScope.launch {
            lastSearchHistory.postValue(localDatabase.getLastSearchResults() as MutableList<String>)
        }
    }

    fun addSearchToLocalDatabase(tag: String) {
        val userID = firebaseAuth.currentUser?.uid ?: ""
        val search = Search(tag, userID, now().toString())
        viewModelScope.launch {
            val tempNewList: MutableList<String> = lastSearchHistory.value ?: mutableListOf()
            localDatabase.insert(search)
            //Se já tiver alguma busca recente com a mesma tag, ele joga ela em primeiro
            if (search.busca in tempNewList) {
                tempNewList.remove(search.busca)
                tempNewList.add(0, search.busca)
            } else if (tempNewList.size >= CONST_MAX_SEARCH_RESULTS) {
                tempNewList.add(0, search.busca)
                tempNewList.removeLast()
            } else {
                tempNewList.add(0, search.busca)
            }
            lastSearchHistory.postValue(tempNewList)
        }
    }

    fun addSearchTag(tag: String) {

    }

    fun addFavoriteChar(characterResult: CharacterResult) {

    }


}