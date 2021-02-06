package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.LocalDatabase
import com.example.projetointegradordigitalhouse.model.ResponseApi
import com.example.projetointegradordigitalhouse.model.Search
import com.example.projetointegradordigitalhouse.model.SearchDao
import com.example.projetointegradordigitalhouse.model.characters.Characters
import com.example.projetointegradordigitalhouse.model.characters.Result
import com.example.projetointegradordigitalhouse.util.Constants.Values.CONST_MAX_SEARCH_RESULTS
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ChipSearchViewModel(
    context: Context
): ViewModel() {
    private val repository: MarvelXRepository by lazy {MarvelXRepository()}
    private val localDatabase: SearchDao by lazy { LocalDatabase.getDatabase(context).userDao() }

    private val firebaseAuth by lazy { Firebase.auth }
    private val firebaseDatabase by lazy { Firebase.firestore }

    var searchCharList: MutableLiveData<List<Result>> = MutableLiveData()
    var lastSearchHistory: MutableLiveData<MutableList<String>> = MutableLiveData()

    fun getCharactersByName(name: String, limit: Int=10, offset:Int=0){
        viewModelScope.launch {
            when (val response = repository.getCharactersByName(name,limit,offset)) {
                is ResponseApi.Success -> {
                    val data = response.data as Characters
                    searchCharList.postValue(data.data.results.take(4))
                }
                is ResponseApi.Error -> {
                }
            }
        }
    }
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
            } else if (tempNewList.size >= CONST_MAX_SEARCH_RESULTS){
                tempNewList.add(0, search.busca)
                tempNewList.removeLast()
            } else {
                tempNewList.add(0, search.busca)
            }
            lastSearchHistory.postValue(tempNewList)
        }
    }

}