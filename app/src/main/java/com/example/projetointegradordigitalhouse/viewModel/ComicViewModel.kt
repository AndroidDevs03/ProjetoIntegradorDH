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

class ComicViewModel(context: Context): ViewModel() {

    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }

    var comicCharList: MutableLiveData<List<CharacterResult>> = MutableLiveData()
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

    fun getComicCharacters(charactersListID: List<Long>) {
        var  comicChars = mutableListOf<CharacterResult>()

        Log.i("ComicrViewModel", "Character List")
        viewModelScope.launch {
            val allCharacters = repository.getAllChars()

            allCharacters?.forEach {
                charactersListID?.forEach { serieID ->
                    if(serieID == it.id){
                        comicChars.add(it)
                        Log.i("ComicViewModel", " ADD: ${it.id} ${it.name} Chars Total - ${comicChars.size}")
                    }
                }
            }
            comicCharList.postValue(comicChars.toList())
        }
    }
    fun addFavorite(result: Any, tabPosition: Int) {
        viewModelScope.launch { repository.addToFavorites(result, tabPosition) }
    }
    fun remFavorite(result: Any, tabPosition: Int) {
        viewModelScope.launch { repository.removeFromFavorites(result, tabPosition) }
    }


}