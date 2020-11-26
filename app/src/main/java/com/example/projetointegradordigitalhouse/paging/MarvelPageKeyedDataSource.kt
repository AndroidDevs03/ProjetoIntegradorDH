package com.example.projetointegradordigitalhouse.paging

import androidx.paging.PageKeyedDataSource
import com.example.projetointegradordigitalhouse.model.MarvelHomeRepository
import com.example.projetointegradordigitalhouse.model.ResponseApi
import com.example.projetointegradordigitalhouse.model.characters.Characters
import com.example.projetointegradordigitalhouse.model.characters.Result
import com.example.projetointegradordigitalhouse.util.Constants.Api.FIRST_PAGE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MarvelPageKeyedDataSource : PageKeyedDataSource<Int, Result>() {
    private val repository by lazy {
        MarvelHomeRepository()
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Result>
    ) {
        CoroutineScope(IO).launch {
            when (val response = repository.getCharacters(FIRST_PAGE)) {
                is ResponseApi.Success -> {
                    val data = response.data as Characters
                    callback.onResult(data.`data`.results, null, FIRST_PAGE + 1)
                }
                is ResponseApi.Error -> {
                    callback.onResult(mutableListOf(), null, FIRST_PAGE + 1)
                }
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Result>
    ) {
        val page = params.key
        CoroutineScope(IO).launch {
            when (val response = repository.getCharacters(page)) {
                is ResponseApi.Success -> {
                    val data = response.data as Characters
                    callback.onResult(data.`data`.results,  page - 1)
                }
                is ResponseApi.Error -> {
                    callback.onResult(mutableListOf(),  page - 1)
                }
            }
        }

    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Result>
    ) {
        val page = params.key
        CoroutineScope(IO).launch {
            when (val response = repository.getCharacters(page)) {
                is ResponseApi.Success -> {
                    val data = response.data as Characters
                    callback.onResult(data.`data`.results,  page + 1)
                }
                is ResponseApi.Error -> {
                    callback.onResult(mutableListOf(), page + 1)
                }
            }
        }
    }

}