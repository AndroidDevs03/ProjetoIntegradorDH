package com.example.projetointegradordigitalhouse.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.ResponseApi
import com.example.projetointegradordigitalhouse.model.characters.Characters
import com.github.cesar1287.desafiopicpayandroid.model.home.HomeBusiness
import kotlinx.coroutines.launch
import com.example.projetointegradordigitalhouse.model.characters.Result

internal class HomeViewModel: ViewModel() {

    private val homeBusiness = HomeBusiness()
    var homeCharList: MutableLiveData<List<Result>> = MutableLiveData()

//  var marvelLiveDataSource: LiveData<PageKeyedDataSource<Int, Result>>? = null

//    init {
//        val marvelDataSourceFactory = MarvelDataSourceFactory()
//        marvelLiveDataSource = marvelDataSourceFactory.getSearchLiveDataSource()
//        val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(PAGE_SIZE).build()
//        charList = LivePagedListBuilder(marvelDataSourceFactory, pagedListConfig).build()
//    }

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
        val tempCharList: MutableList<Result> = mutableListOf()
        viewModelScope.launch {
            when (val response = homeBusiness.getCharactersByID(1009664)) {
                is ResponseApi.Success -> {
                    val data = response.data as Characters
                    tempCharList.add(data.data.results[0])
                }
                is ResponseApi.Error -> {
                }
            }
            when (val response = homeBusiness.getCharactersByID(1009610)) {
                is ResponseApi.Success -> {
                    val data = response.data as Characters
                    tempCharList.add(data.data.results[0])
                }
                is ResponseApi.Error -> {
                }
            }
            when (val response = homeBusiness.getCharactersByID(1009268)) {
                is ResponseApi.Success -> {
                    val data = response.data as Characters
                    tempCharList.add(data.data.results[0])
                }
                is ResponseApi.Error -> {
                }
            }
            when (val response = homeBusiness.getCharactersByID(1009368)) {
                is ResponseApi.Success -> {
                    val data = response.data as Characters
                    tempCharList.add(data.data.results[0])
                }
                is ResponseApi.Error -> {
                }
            }
            when (val response = homeBusiness.getCharactersByID(1009189)) {
                is ResponseApi.Success -> {
                    val data = response.data as Characters
                    tempCharList.add(data.data.results[0])
                }
                is ResponseApi.Error -> {
                }
            }
            homeCharList.postValue(tempCharList)
        }
    }
}