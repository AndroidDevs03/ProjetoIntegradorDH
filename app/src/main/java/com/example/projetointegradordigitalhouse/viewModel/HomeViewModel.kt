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
import com.google.firebase.auth.FirebaseUser

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
            } else if (tempNewList.size >= Constants.Values.CONST_MAX_SEARCH_RESULTS){
                tempNewList.add(0, search.busca)
                tempNewList.removeLast()
            } else {
                tempNewList.add(0, search.busca)
            }
            lastSearchHistory.postValue(tempNewList)
        }
    }
    fun getHomeCharacters() {
//    1009664 Thor
//    1009610 Spider-Man
//    1009268 Deadpool
//    1009368 Iron Man
//    1009351 Hulk
//    1009718 Wolverine
//    1009282 Doctor Strange
//    1009220 Captain America
//    1010743 Groot
//    1009187 Black Panther
//    1009562 Scarlet Witch
//    Ant-Man (não teve retorno)
//    1010744 Rocket Raccoon
//    1009189 Black Widow
//    1009515 Punisher
//    Ghost Rider (não teve retorno)
//    1009592 Silver Surfer
//    Star-Lord (não teve retorno)
//    1009697 Vision
//    Phoenix (não teve retorno)
//    1009338 Hawkeye
//    1009313 Gambit
//    1009472 Nightcrawler
//    Ms. Marvel (não teve retorno)
//    1009504 Professor X
        viewModelScope.launch {
            val tempList = repository.getMostPopularCharacters(5)
            Log.i("HomeViewModel", "Character List com ${tempList.size} elementos")
            homeCharList.postValue(tempList)
        }
    }
    fun getHomeSeries() {
        Log.i("HomeViewModel", "Series List")
        viewModelScope.launch {
            val tempList = repository.getMostPopularSeries(5)
            homeSeriesList.postValue(tempList)
        }
    }
    fun getHomeComics() {
        Log.i("HomeViewModel", "Comic List")
        viewModelScope.launch {
            val tempList = repository.getMostPopularComics(5)
            homeComicsList.postValue(tempList)
        }
    }
}