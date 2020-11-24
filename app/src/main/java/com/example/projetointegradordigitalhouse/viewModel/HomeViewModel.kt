package com.example.projetointegradordigitalhouse.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.example.projetointegradordigitalhouse.model.ResponseApi
import com.github.cesar1287.desafiopicpayandroid.model.home.HomeBusiness
import kotlinx.coroutines.launch
import com.example.projetointegradordigitalhouse.model.characters.Result
import com.example.projetointegradordigitalhouse.util.Constants.Api.PAGE_SIZE

internal class HomeViewModel: ViewModel() {

    private val homeBusiness = HomeBusiness()
    private var charList: LiveData<PagedList<Result>>?= null
    private var marvelLiveDataSource: LiveData<PageKeyedDataSource<Int, Result>>? = null

    init {
        val marvelDataSourceFactory = MarvelDataSourceFactory()
        marvelLiveDataSource = MarvelDataSourceFactory.getSearchLiveDataSource()
        val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(PAGE_SIZE).build()
        charList = LivePagedListBuilder(marvelDataSourceFactory, pagedListConfig).build()
    }

    fun getCharacters() {
        viewModelScope.launch {
            when (val response = homeBusiness.getCharacters()) {
                is ResponseApi.Success -> {
//                    usersLiveData.postValue(
////                        response.data as? Users
//                    )
                }
                is ResponseApi.Error -> {
//                    errorMessageLiveData.postValue(
//                        response.message
//                    )
                }
            }
        }
    }
}