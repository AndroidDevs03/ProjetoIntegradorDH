package com.github.cesar1287.desafiopicpayandroid.model.home

import com.example.projetointegradordigitalhouse.model.ApiService
import com.example.projetointegradordigitalhouse.model.ResponseApi
import java.lang.Exception

class HomeRepository {

    suspend fun getCharacters(): ResponseApi {
        return try {
            val response = ApiService.marvelApi.Characters()

            if (response.isSuccessful) {
                ResponseApi.Success(response.body())
            } else {
                if (response.code() == 404) {
                    ResponseApi.Error("Dado não encontrado")
                } else {
                    ResponseApi.Error("Erro ao carregar os dados")
                }
            }
        } catch (exception: Exception) {
            ResponseApi.Error("Erro ao carregar os dados")
        }
    }
}