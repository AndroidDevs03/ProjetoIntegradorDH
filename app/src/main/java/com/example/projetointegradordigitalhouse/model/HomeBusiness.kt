package com.github.cesar1287.desafiopicpayandroid.model.home

import com.example.projetointegradordigitalhouse.model.ResponseApi

class HomeBusiness {

    private val repository: MarvelXRepository by lazy {
        MarvelXRepository()
    }

    suspend fun getCharactersByName(name: String): ResponseApi {
        return repository.getCharactersByName(name)
    }
    suspend fun getCharactersByID(id: Int): ResponseApi {
        return repository.getCharactersByID(id)
    }
}