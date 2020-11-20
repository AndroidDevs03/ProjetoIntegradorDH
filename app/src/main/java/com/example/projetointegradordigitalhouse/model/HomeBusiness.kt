package com.github.cesar1287.desafiopicpayandroid.model.home

import com.example.projetointegradordigitalhouse.model.ResponseApi

class HomeBusiness {

    private val repository: HomeRepository by lazy {
        HomeRepository()
    }

    suspend fun getCharacters(): ResponseApi {
        return repository.getCharacters()
    }
}