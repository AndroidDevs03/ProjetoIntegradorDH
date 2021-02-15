package com.github.cesar1287.desafiopicpayandroid.model.home

import android.content.Context
import android.util.Log
import com.example.projetointegradordigitalhouse.model.*
import com.example.projetointegradordigitalhouse.model.characters.CharacterResponse
import com.example.projetointegradordigitalhouse.model.comics.ComicResponse
import com.example.projetointegradordigitalhouse.model.series.SeriesResponse
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_CHARACTER_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_COMICS_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_SERIES_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_USER_ID
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.time.LocalDate.now
import java.time.LocalDateTime

class MarvelXRepository(context: Context) {

    private val firebaseFirestore: FirebaseFirestore by lazy { FirebaseFirestore() }
    private val firebaseAuth by lazy { Firebase.auth }
    private val localDatabaseSearch: SearchDao by lazy { LocalDatabase.getDatabase(context).searchDao() }
    private val localDatabaseFavorite: FavoriteDao by lazy { LocalDatabase.getDatabase(context).favoriteDao() }
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

    suspend fun searchByName(
        tag: String,
        limit: Int = 10,
        offset: Int = 0
    ): Pair<MutableList<CharacterResult> , MutableList<SeriesResult>> {
        var tempCharList = mutableListOf<CharacterResult>()
        var tempSeriesList = mutableListOf<SeriesResult>()
        // Primeiro, verifica se essa busca já foi feita no histórico do Firebase. Caso positivo, verifica se foi há mais de dois dias.
        if (firebaseFirestore.lastSearchNeedsUpdate(tag)) {
            // Se foi há mais de dois dias, refaz a busca na api da Marvel e atualiza o Firebase
            val responseChars = marvelApi.charactersByName(tag, limit, offset)
            val responseSeries = marvelApi.seriesByName(tag, limit, offset)
            if (responseChars.isSuccessful) {
                tempCharList = convertResponseToCharList(responseChars.body())
                tempCharList.forEach { firebaseFirestore.insertCharacter(it) }
                Log.i("Repository", "${tempCharList.size} Characters updated to Firebase")
                tempCharList.forEach { updateSeriesByCharacterID(it.id) }
            }
            if (responseSeries.isSuccessful) {
                tempSeriesList = convertResponseToSeriesList(responseSeries.body())
                tempSeriesList.forEach { firebaseFirestore.insertSeries(it) }
                Log.i("Repository", "${tempSeriesList.size} Series updated to Firebase")
                tempSeriesList.forEach { updateComicsBySeriesID(it.id) }
            }
            Log.i("Repository", "${tag} Search Tag updated to Firebase")
            firebaseFirestore.insertSearchTag(tag)
            return Pair(tempCharList,tempSeriesList)
        } else {
            // Se foi há menos de dois dias, busca os dados no Firebase.
            allChars = firebaseFirestore.getAllChars()
            allSeries = firebaseFirestore.getAllSeries()
            tempCharList = allChars.filter { it.name.contains(tag) } as MutableList<CharacterResult>
            tempSeriesList = allSeries.filter { it.name.contains(tag) } as MutableList<SeriesResult>
        }
        return Pair(tempCharList,tempSeriesList)
    }

    suspend fun updateSeriesByCharacterID(charID: Int) {
        val response = marvelApi.seriesByCharacterID(charID)
        if (response.isSuccessful) {
            val tempSeries = convertResponseToSeriesList(response.body())
            tempSeries.forEach { firebaseFirestore.insertSeries(it) }
            Log.i("Repository", "${tempSeries.size} Series updated to Firebase")
        }
    }

    suspend fun updateComicsBySeriesID(seriesId: Int) {
        val response = marvelApi.comicsBySeriesID(seriesId)
        if (response.isSuccessful) {
            val tempComics = convertResponseToComicList(response.body())
            tempComics.forEach { firebaseFirestore.insertComic(it) }
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
        val data =
            body?.data?.results as List<com.example.projetointegradordigitalhouse.model.characters.Result>
        val tempList = mutableListOf<CharacterResult>()
        data.forEach {
            if (it.series.available != 0) {  //pega apenas os personagens que aparecem em pelo menos uma série
                val tempSeriesList = mutableListOf<Long>()
                it.series.items.forEach { itSeries ->
                    tempSeriesList.add(itSeries.resourceURI.split("/").last().toLong())
                }
                it.description = it.description ?: "Description not found"
                tempList.add(
                    CharacterResult(
                        it.id,
                        it.name,
                        it.thumbnail.getThumb(),
                        it.description,
                        searchTagFlag = false,
                        favoriteTagFlag = false,
                        lastUpdate = LocalDateTime.now().toString(),
                        series = tempSeriesList
                    )
                )
            }
        }
        return tempList
    }

    private fun convertResponseToSeriesList(body: SeriesResponse?): MutableList<SeriesResult> {
        val data =
            body?.data?.results as List<com.example.projetointegradordigitalhouse.model.series.Result>
        val tempList = mutableListOf<SeriesResult>()
        data.forEach {
            if (it.comics.available != 0) {  //pega apenas os personagens que aparecem em pelo menos uma série
                val tempCharactersList = mutableListOf<Long>()
                val tempComicsList = mutableListOf<Long>()
                it.characters.items.forEach { itSeries ->
                    tempCharactersList.add(itSeries.resourceURI.split("/").last().toLong())
                }
                it.comics.items.forEach { itSeries ->
                    tempComicsList.add(itSeries.resourceURI.split("/").last().toLong())
                }
                it.description = it.description ?: "Description not found"
                tempList.add(
                    SeriesResult(
                        it.id,
                        it.title,
                        it.thumbnail.getThumb(),
                        it.description.toString(),
                        searchTagFlag = false,
                        favoriteTagFlag = false,
                        lastUpdate = LocalDateTime.now().toString(),
                        charactersList = tempCharactersList,
                        comicsList = tempComicsList
                    )
                )
            }

        }
        return tempList
    }

    private fun convertResponseToComicList(body: ComicResponse?): MutableList<ComicResult> {
        val data =
            body?.data?.results as List<com.example.projetointegradordigitalhouse.model.comics.Result>
        val tempList = mutableListOf<ComicResult>()
        data.forEach {
            if (it.characters.available != 0) {  //pega apenas as séries que tem pelo menos um personagem
                val tempCharactersList = mutableListOf<Long>()
                it.characters.items.forEach { itSeries ->
                    tempCharactersList.add(itSeries.resourceURI.split("/").last().toLong())
                }
                it.description = it.description ?: "Description not found"

                val tempComic = ComicResult(
                    it.id,
                    it.title,
                    it.thumbnail.getThumb(),
                    it.description,
                    searchTagFlag = false,
                    favoriteTagFlag = false,
                    lastUpdate = LocalDateTime.now().toString(),
                    charactersList = tempCharactersList as List<Long>,
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

    suspend fun addToFavorites(result: Any, tabPosition: Int) {
        val userID = firebaseAuth.currentUser?.uid ?: ""
        // 1) Adiciona o favorito na database local
        when (tabPosition) {
            0 -> { localDatabaseFavorite.insertChar(convertResultToFavoriteChar(result as CharacterResult)) }
            1 -> { localDatabaseFavorite.insertSeries(convertResultToFavoriteSeries(result as SeriesResult)) }
            2 -> { localDatabaseFavorite.insertComic(convertResultToFavoriteComic(result as ComicResult)) }
        }

        // 2) Incrementa o item no Firebase
        firebaseFirestore.incrementFavorited((result as GeneralResult).id, tabPosition)

        // 3) Atualiza a lista de favoritos do Firebase
        // 3.1) Recupera a lista atualizada da database local
        val newFavoriteList = mutableListOf<Int>()
        when (tabPosition) {
            0 -> { localDatabaseFavorite.getAllFavoriteCharacters(userID).forEach { newFavoriteList.add(it.id) } }
            1 -> { localDatabaseFavorite.getAllFavoriteSeries(userID).forEach { newFavoriteList.add(it.id) } }
            2 -> { localDatabaseFavorite.getAllFavoriteComics(userID).forEach { newFavoriteList.add(it.id) } }
        }
        // 3.2) atualiza a lista no firebase
        firebaseFirestore.updateFavoriteList(newFavoriteList, tabPosition)
    }

    suspend fun removeFromFavorites(result: Any, tabPosition: Int) {
        val userID = firebaseAuth.currentUser?.uid ?: ""
        // 1) Adiciona o favorito na database local
        when (tabPosition) {
            0 -> { localDatabaseFavorite.insertChar(convertResultToFavoriteChar(result as CharacterResult)) }
            1 -> { localDatabaseFavorite.insertSeries(convertResultToFavoriteSeries(result as SeriesResult)) }
            2 -> { localDatabaseFavorite.insertComic(convertResultToFavoriteComic(result as ComicResult)) }
        }

        // 2) Decrementa o item no Firebase
        firebaseFirestore.decrementFavorited((result as GeneralResult).id, tabPosition)

        // 3) Atualiza a lista de favoritos do Firebase
        // 3.1) Recupera a lista atualizada da database local
        val newFavoriteList = mutableListOf<Int>()
        when (tabPosition) {
            0 -> { localDatabaseFavorite.getAllFavoriteCharacters(userID).forEach { newFavoriteList.add(it.id) } }
            1 -> { localDatabaseFavorite.getAllFavoriteSeries(userID).forEach { newFavoriteList.add(it.id) } }
            2 -> { localDatabaseFavorite.getAllFavoriteComics(userID).forEach { newFavoriteList.add(it.id) } }
        }
        // 3.2) atualiza a lista no firebase
        firebaseFirestore.updateFavoriteList(newFavoriteList, tabPosition)
    }
    suspend fun convertResultToFavoriteChar(result: CharacterResult): FavoriteChar {
        val userID = firebaseAuth.currentUser?.uid ?: ""
        return FavoriteChar(
            result.id,
            userID,
            result.name,
            result.thumbnail,
            result.description,
            result.favoriteTagFlag
        )
    }
    suspend fun convertResultToFavoriteSeries(result: SeriesResult): FavoriteSeries {
        val userID = firebaseAuth.currentUser?.uid ?: ""
        return FavoriteSeries(
            result.id,
            userID,
            result.name,
            result.thumbnail,
            result.description,
            result.favoriteTagFlag
        )
    }
    suspend fun convertResultToFavoriteComic(result: ComicResult): FavoriteComic {
        val userID = firebaseAuth.currentUser?.uid ?: ""
        return FavoriteComic(
            result.id,
            userID,
            result.name,
            result.thumbnail,
            result.description,
            result.favoriteTagFlag
        )
    }

//    suspend fun getAllFavorites(userID: String): MutableList<Any> {
//        val favoriteCharacters = localDatabaseFavorite.getAllFavoriteCharacters(userID)
//        val favoriteSeries = localDatabaseFavorite.getAllFavoriteSeries(userID)
//        val favoriteComics = localDatabaseFavorite.getAllFavoriteComics(userID)
//
//        return mutableListOf(favoriteCharacters,favoriteSeries,favoriteComics)
//    }
    suspend fun getFavoritesCharacters(userID: String): MutableSet<FavoriteChar> {
        val tempLocal = localDatabaseFavorite.getAllFavoriteCharacters(userID)

        return tempLocal.toMutableSet()
    }
    suspend fun getFavoritesSeries(userID: String): MutableSet<FavoriteSeries> {
        val temp = localDatabaseFavorite.getAllFavoriteSeries(userID)
        return temp.toMutableSet()
    }
    suspend fun getFavoritesComics(userID: String): MutableSet<FavoriteComic> {
        val temp = localDatabaseFavorite.getAllFavoriteComics(userID)
        return temp.toMutableSet()
    }

    suspend fun setUser(user: User) {
        try {
            firebaseFirestore.insertUser(user)
        } catch (exception: Exception) {
//            todo
        }
    }


    }

