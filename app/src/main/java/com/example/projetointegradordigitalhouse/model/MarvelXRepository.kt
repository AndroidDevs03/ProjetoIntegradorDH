package com.github.cesar1287.desafiopicpayandroid.model.home

import com.example.projetointegradordigitalhouse.model.ApiService
import com.example.projetointegradordigitalhouse.model.FirebaseFirestore
import com.example.projetointegradordigitalhouse.model.MarvelApi
import com.example.projetointegradordigitalhouse.model.ResponseApi
import java.lang.Exception

class MarvelXRepository {

    private val firebaseFirestore: FirebaseFirestore by lazy { FirebaseFirestore() }
    private val marvelApi: MarvelApi by lazy { MarvelApi() }

    suspend fun getCharactersByName(name: String,limit:Int=10, offset:Int=0): ResponseApi {
        return try {
            val response = ApiService.marvelApi.CharactersByName(name,limit,offset)

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
    suspend fun getSeriesByName(name: String,limit:Int=10, offset:Int=0): ResponseApi {
        return try {
            val response = ApiService.marvelApi.SeriesByName(name,limit,offset)

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
    suspend fun getComicsByName(name: String,limit:Int=10, offset:Int=0): ResponseApi {
        return try {
            val response = ApiService.marvelApi.ComicsByName(name,limit,offset)

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
    suspend fun getCharactersByID(id:Int): ResponseApi {
        return try {
            val response = ApiService.marvelApi.CharactersByID(id)

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