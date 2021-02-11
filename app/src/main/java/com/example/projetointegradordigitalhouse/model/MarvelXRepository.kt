package com.github.cesar1287.desafiopicpayandroid.model.home

import android.util.Log
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.model.characters.CharacterResponse
import com.example.projetointegradordigitalhouse.model.comics.ComicResponse
import com.example.projetointegradordigitalhouse.model.series.SeriesResponse
import java.lang.Exception
import java.time.LocalDate.now

class MarvelXRepository {

    private val firebaseFirestore: FirebaseFirestore by lazy { FirebaseFirestore() }
    private val marvelApi = MarvelApi.commands
    private lateinit var allChars: MutableList<CharacterResult>
    private lateinit var allSeries: MutableList<SeriesResult>
    private lateinit var allComics: MutableList<ComicResult>

    suspend fun getMostPopularCharacters(limit: Long): MutableList<CharacterResult> {
        return firebaseFirestore.getMostPopularCharacters(limit)
    }
    suspend fun getMostPopularSeries(limit: Long): MutableList<SeriesResult> {
        return firebaseFirestore.getMostPopularSeries(limit)
    }
    suspend fun getMostPopularComics(limit: Long): MutableList<ComicResult> {
        return firebaseFirestore.getMostPopularComics(limit)
    }
    suspend fun getCharactersByName(
        tag: String,
        limit: Int = 10,
        offset: Int = 0
    ): MutableList<CharacterResult> {
        var tempCharList = mutableListOf<CharacterResult>()
        // Primeiro, verifica se essa busca já foi feita no histórico do Firebase. Caso positivo, verifica se foi há mais de dois dias.
        if (firebaseFirestore.lastSearchNeedsUpdate(tag)) {
            // Se foi há mais de dois dias, refaz a busca na api da Marvel e atualiza o Firebase
            val response = marvelApi.charactersByName(tag, limit, offset)
            if (response.isSuccessful) {
                tempCharList = convertResponseToCharList(response.body())
                tempCharList.forEach {
                    firebaseFirestore.insertCharacter(it)

                    // it.series.forEach { itSeries -> getSeriesByCharacterID(itSeries)}
                }
                Log.i("Repository", "${tempCharList.size} Characters updated to Firebase")

                Log.i("Repository", "${tag} Search Tag updated to Firebase")
                firebaseFirestore.insertSearchTag(tag)
                return tempCharList
            }
        } else {
            // Se foi há menos de dois dias, busca os dados no Firebase.
            allChars = firebaseFirestore.getAllChars()
            tempCharList = allChars.filter {
                it.name.contains(tag)
            } as MutableList<CharacterResult>
        }
        return tempCharList
    }

    suspend fun updateSeriesByCharacterID(charID: Int) {
        val response = marvelApi.seriesByCharacterID(charID)
        if (response.isSuccessful) {
            val tempSeries = convertResponseToSeriesList(response.body())
            tempSeries.forEach { firebaseFirestore.insertSeries(it)}
            Log.i("Repository", "${tempSeries.size} Series updated to Firebase")
        }
    }

    suspend fun updateComicsBySeriesID(seriesId: Int) {
        val response = marvelApi.comicsBySeriesID(seriesId)
        if (response.isSuccessful) {
            val tempComics = convertResponseToComicList(response.body())
            tempComics.forEach { firebaseFirestore.insertComic(it)}
            Log.i("Repository", "${tempComics.size} Comics updated to Firebase")
        }
    }

    suspend fun getSeriesByName(
        tag: String,
        limit: Int = 10,
        offset: Int = 0
    ): MutableList<SeriesResult> {
        var tempSeriesList = mutableListOf<SeriesResult>()
        // Primeiro, verifica se essa busca já foi feita no histórico do Firebase. Caso positivo, verifica se foi há mais de dois dias.
        if (firebaseFirestore.lastSearchNeedsUpdate(tag)) {
            // Se foi há mais de dois dias, refaz a busca na api da Marvel e atualiza o Firebase
            val response = marvelApi.seriesByName(tag, limit, offset)
            if (response.isSuccessful) {
                tempSeriesList = convertResponseToSeriesList(response.body())
                tempSeriesList.forEach {
                    firebaseFirestore.insertSeries(it)
                    //Todo:  Buscar as HQs
                }
                return tempSeriesList
            }
        } else {
            // Se foi há menos de dois dias, busca os dados no Firebase.
            allSeries = firebaseFirestore.getAllSeries()
            tempSeriesList = allSeries.filter {
                it.name.contains(tag)
            } as MutableList<SeriesResult>
        }
        firebaseFirestore.insertSearchTag(tag)
        return tempSeriesList
    }
    suspend fun getComicsByName(
        tag: String,
        limit: Int = 10,
        offset: Int = 0
    ): MutableList<ComicResult> {
        var tempComicList = mutableListOf<ComicResult>()
        // Primeiro, verifica se essa busca já foi feita no histórico do Firebase. Caso positivo, verifica se foi há mais de dois dias.
        if (firebaseFirestore.lastSearchNeedsUpdate(tag)) {
            // Se foi há mais de dois dias, refaz a busca na api da Marvel e atualiza o Firebase
            val response = marvelApi.comicsByName(tag, limit, offset)
            if (response.isSuccessful) {
                tempComicList = convertResponseToComicList(response.body())
                tempComicList.forEach { firebaseFirestore.insertComic(it) }
                return tempComicList
            }
        } else {
            // Se foi há menos de dois dias, busca os dados no Firebase.
            allComics = firebaseFirestore.getAllComics()
            tempComicList = allComics.filter {
                it.name.contains(tag)
            } as MutableList<ComicResult>
        }
        firebaseFirestore.insertSearchTag(tag)
        return tempComicList
    }
    private fun convertResponseToCharList(body: CharacterResponse?): MutableList<CharacterResult> {
        val data = body?.data?.results as List<com.example.projetointegradordigitalhouse.model.characters.Result>
        val tempList = mutableListOf<CharacterResult>()
        data.forEach {
            if (it.series.available != 0){  //pega apenas os personagens que aparecem em pelo menos uma série
                val tempSeriesList = mutableListOf<Int>()
                it.series.items.forEach { itSeries ->
                    tempSeriesList.add(itSeries.resourceURI.split("/").last().toInt())
                }
                it.description = it.description?: "Description not found"
                tempList.add(
                    CharacterResult(
                        it.id,
                        it.name,
                        it.thumbnail.getThumb(),
                        it.description,
                        searchTagFlag = false,
                        favoriteTagFlag = false,
                        lastUpdate = now().toString(),
                        series = tempSeriesList
                    )
                )
            }
        }
        return tempList
    }
    private fun convertResponseToSeriesList(body: SeriesResponse?): MutableList<SeriesResult> {
        val data = body?.data?.results as List<com.example.projetointegradordigitalhouse.model.series.Result>
        val tempList = mutableListOf<SeriesResult>()
        data.forEach {
            if (it.comics.available != 0){  //pega apenas os personagens que aparecem em pelo menos uma série
                val tempCharactersList = mutableListOf<Int>()
                val tempComicsList = mutableListOf<Int>()
                it.characters.items.forEach { itSeries ->
                    tempCharactersList.add(itSeries.resourceURI.split("/").last().toInt())
                }
                it.comics.items.forEach { itSeries ->
                    tempComicsList.add(itSeries.resourceURI.split("/").last().toInt())
                }
                it.description = it.description?: "Description not found"
                tempList.add(
                    SeriesResult(
                        it.id,
                        it.title,
                        it.thumbnail.getThumb(),
                        it.description.toString(),
                        searchTagFlag = false,
                        favoriteTagFlag = false,
                        lastUpdate = now().toString(),
                        charactersList = tempCharactersList,
                        comicsList = tempComicsList
                    )
                )
            }

        }
        return tempList
    }
    private fun convertResponseToComicList(body: ComicResponse?): MutableList<ComicResult> {
        val data = body?.data?.results as List<com.example.projetointegradordigitalhouse.model.comics.Result>
        val tempList = mutableListOf<ComicResult>()
        data.forEach {
            if (it.characters.available != 0){  //pega apenas as séries que tem pelo menos um personagem
                val tempCharactersList = mutableListOf<Int>()
                it.characters.items.forEach { itSeries ->
                    tempCharactersList.add(itSeries.resourceURI.split("/").last().toInt())
                }
                it.description = it.description?: "Description not found"

                val tempComic = ComicResult(
                    it.id,
                    it.title,
                    it.thumbnail.getThumb(),
                    it.description,
                    searchTagFlag = false,
                    favoriteTagFlag = false,
                    lastUpdate = now().toString(),
                    charactersList = tempCharactersList as List<Int>,
                    pageCount = it.pageCount.toString(),
                    issueNumber = it.issueNumber.toString(),
                    seriesID = it.series.resourceURI.split("/").last().toLong(),
                    published = it.dates[0].date,
                    price = it.prices[0].price
                )
                tempList.add(tempComic)
            }
        }
        return tempList
    }

    suspend fun getCharactersByID(id: Int): ResponseApi {
        return try {
            val response = marvelApi.charactersByID(id)

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