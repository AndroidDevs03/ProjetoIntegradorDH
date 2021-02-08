package com.github.cesar1287.desafiopicpayandroid.model.home

import android.util.Log
import com.example.projetointegradordigitalhouse.model.*
import com.google.type.DateTime
import retrofit2.Retrofit
import java.lang.Exception
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDate.parse
import java.util.*

class MarvelXRepository {

    private val firebaseFirestore: FirebaseFirestore by lazy { FirebaseFirestore() }
    private val marvelApi = MarvelApi.commands

    suspend fun getMostPopularCharacters(limit: Long):MutableList<CharacterResult>{ return firebaseFirestore.getMostPopularCharacters(limit)}
    suspend fun getMostPopularSeries(limit: Long):MutableList<SeriesResult>{return firebaseFirestore.getMostPopularSeries(limit)}
    suspend fun getMostPopularComics(limit: Long):MutableList<ComicResult>{return firebaseFirestore.getMostPopularComics(limit)}

//    val list =
//        for (characterResult in list) {
//            if (parse(characterResult.lastUpdate).isBefore(now().minusDays(2L))) {
//
//            }
//        }

    suspend fun getCharactersByName(name: String,limit:Int=10, offset:Int=0): ResponseApi {
        return try {
            val response = marvelApi.CharactersByName(name,limit,offset)

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
            val response = marvelApi.SeriesByName(name,limit,offset)

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
            val response = marvelApi.ComicsByName(name,limit,offset)

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
            val response = marvelApi.CharactersByID(id)

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