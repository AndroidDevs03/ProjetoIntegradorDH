package com.example.projetointegradordigitalhouse.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projetointegradordigitalhouse.model.characters.Result
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository

class NameSearchViewModel: ViewModel()  {
    private val repository: MarvelXRepository by lazy {
        MarvelXRepository()
    }
    var searchCharList: MutableLiveData<List<Result>> = MutableLiveData()

//    fun getCharactersByName(name: String, limit: Int=10, offset:Int=0){
//        viewModelScope.launch {
//            when (val response = repository.getCharactersByName(name,limit,offset)) {
//                is ResponseApi.Success -> {
//                    val data = response.data as Characters
//                    searchCharList.postValue(data.data.results)
//                }
//                is ResponseApi.Error -> {
//                }
//            }
//        }
//    }
}