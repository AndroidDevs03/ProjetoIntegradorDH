package com.example.projetointegradordigitalhouse.paging

import androidx.paging.PageKeyedDataSource
import com.example.projetointegradordigitalhouse.model.characters.Result

class MarvelPageKeyedDataSource: PageKeyedDataSource<Int, Result>() {
    private val repository by lazy {
        MarvelHomeRepository()
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Result>
    ) {
        TODO("Not yet implemented")
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ???>) {
        TODO("Not yet implemented")
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ???>) {
        TODO("Not yet implemented")
    }

}