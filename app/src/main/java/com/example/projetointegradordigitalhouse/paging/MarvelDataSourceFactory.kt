package com.example.projetointegradordigitalhouse.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.projetointegradordigitalhouse.model.characters.Result

class MarvelDataSourceFactory: DataSource.Factory<Int, Result>() {
    private val marvelLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, Result>>()
    override fun create(): DataSource<Int, Result> {
        val marvelDataSource = MarvelPageKeyedDataSource()
        marvelLiveDataSource.postValue(marvelDataSource)
        return marvelDataSource
    }
    fun getSearchLiveDataSource(): MutableLiveData<PageKeyedDataSource<Int, Result>>{
        return marvelLiveDataSource
    }

}