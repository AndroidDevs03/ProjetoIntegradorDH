package com.example.projetointegradordigitalhouse.model

import android.util.Log
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_CHARACTER_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_CHARACTER_LIST
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_COMICS_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_COMIC_LIST
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_DESCRIPTION
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_FAVORITED
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_ISSUE_NUMBER
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_LAST_UPDATE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_NAME
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_PAGE_COUNT
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_PRICE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_PUBLISHED
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_SERIES_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_SERIES_ID
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_THUMBNAIL
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseFirestore {

    private val firebaseDatabase by lazy { Firebase.firestore }

    suspend fun getMostPopularCharacters(limit: Long): MutableList<CharacterResult> = suspendCoroutine{ ret ->
        val tempList = mutableListOf<CharacterResult>()
        firebaseDatabase.collection(NAME_CHARACTER_DATABASE).orderBy(NAME_FAVORITED).limit(limit)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tempData = CharacterResult(
                        document.id.toInt(),
                        document[NAME_NAME].toString(),
                        document[NAME_THUMBNAIL].toString(),
                        document[NAME_DESCRIPTION].toString(),
                        false,
                        false,
                        document[NAME_LAST_UPDATE].toString()
                    )
                    tempList.add(tempData)
                }
                Log.i("Firebase", "Dados recebidos com sucesso. Enviando lista de Characters com ${tempList.size} elementos")
                ret.resume(tempList)
            }
            .addOnFailureListener { exception ->
                Log.i("Firebase", "Erro ao receber dados do Firebase. ",exception)
            }
    }

    suspend fun getMostPopularSeries(limit: Long): MutableList<SeriesResult>  = suspendCoroutine{ ret ->
        val tempList = mutableListOf<SeriesResult>()
        firebaseDatabase.collection(NAME_SERIES_DATABASE).orderBy(NAME_FAVORITED).limit(limit).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tempData = SeriesResult(
                        document.id.toInt(),
                        document[NAME_NAME].toString(),
                        document[NAME_THUMBNAIL].toString(),
                        document[NAME_DESCRIPTION].toString(),
                        false,
                        false,
                        document[NAME_LAST_UPDATE].toString(),
                        document[NAME_CHARACTER_LIST] as List<Int>,
                        document[NAME_COMIC_LIST] as List<Int>
                    )
                    tempList.add(tempData)
                }
                Log.i("Firebase", "Dados recebidos com sucesso. Enviando lista de Series com ${tempList.size} elementos")
                ret.resume(tempList)
            }
    }

    suspend fun getMostPopularComics(limit: Long): MutableList<ComicResult>  = suspendCoroutine{ ret ->
        val tempList = mutableListOf<ComicResult>()
        firebaseDatabase.collection(NAME_COMICS_DATABASE).orderBy(NAME_FAVORITED).limit(limit).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tempData = ComicResult(

                        document.id.toInt(),
                        document[NAME_NAME].toString(),
                        document[NAME_THUMBNAIL].toString(),
                        document[NAME_DESCRIPTION].toString(),
                        false,
                        false,
                        document[NAME_LAST_UPDATE].toString(),
                        document[NAME_CHARACTER_LIST] as List<Int>,
                        document[NAME_PAGE_COUNT] as String,
                        document[NAME_ISSUE_NUMBER] as String,
                        document[NAME_SERIES_ID] as Long,
                        document[NAME_PUBLISHED] as String,
                        document[NAME_PRICE] as Double
                    )
                    tempList.add(tempData)
                }
                Log.i("Firebase", "Dados recebidos com sucesso. Enviando lista de Comics com ${tempList.size} elementos")
                ret.resume(tempList)
            }
    }

    fun insertCharacter(result: CharacterResult) {
        val dataTemp: HashMap<String, Any> = HashMap()
        dataTemp[NAME_NAME] = result.name
        dataTemp[NAME_THUMBNAIL] = result.thumbnail
        dataTemp[NAME_DESCRIPTION] = result.description

        firebaseDatabase.collection(NAME_CHARACTER_DATABASE).document(result.id.toString())
            .set(dataTemp, SetOptions.merge())
    }

    fun insertSeries(result: SeriesResult) {
        val dataTemp: HashMap<String, Any> = HashMap()
        dataTemp[NAME_NAME] = result.name
        dataTemp[NAME_THUMBNAIL] = result.thumbnail
        dataTemp[NAME_DESCRIPTION] = result.description
        dataTemp[NAME_CHARACTER_LIST] = result.charactersList
        dataTemp[NAME_COMIC_LIST] = result.comicsList

        firebaseDatabase.collection(NAME_SERIES_DATABASE).document(result.id.toString())
            .set(dataTemp, SetOptions.merge())
    }

    fun insertComic(result: ComicResult) {
        val dataTemp: HashMap<String, Any> = HashMap()
        dataTemp[NAME_NAME] = result.name
        dataTemp[NAME_THUMBNAIL] = result.thumbnail
        dataTemp[NAME_DESCRIPTION] = result.description
        dataTemp[NAME_CHARACTER_LIST] = result.charactersList
        dataTemp[NAME_ISSUE_NUMBER] = result.issueNumber
        dataTemp[NAME_PAGE_COUNT] = result.pageCount
        dataTemp[NAME_PUBLISHED] = result.published
        dataTemp[NAME_SERIES_ID] = result.seriesID
        dataTemp[NAME_PRICE] = result.price

        firebaseDatabase.collection(NAME_COMICS_DATABASE).document(result.id.toString())
            .set(dataTemp, SetOptions.merge())
    }

    fun incrementFavorited(result: GeneralResult) {
        val database = when (result) {
            is CharacterResult -> NAME_CHARACTER_DATABASE
            is SeriesResult -> NAME_SERIES_DATABASE
            is ComicResult -> NAME_COMICS_DATABASE
            else -> ""
        }
        firebaseDatabase.collection(database).document(result.id.toString()).get()
            .addOnSuccessListener {
                val favorited = it[NAME_FAVORITED] as Long
                val dataTemp = hashMapOf(NAME_FAVORITED to (favorited + 1))
                firebaseDatabase.collection(database).document(result.id.toString())
                    .set(dataTemp, SetOptions.merge())
            }
    }

    fun decrementFavorited(result: GeneralResult) {
        val database = when (result) {
            is CharacterResult -> NAME_CHARACTER_DATABASE
            is SeriesResult -> NAME_SERIES_DATABASE
            is ComicResult -> NAME_COMICS_DATABASE
            else -> ""
        }
        firebaseDatabase.collection(database).document(result.id.toString()).get()
            .addOnSuccessListener {
                val favorited = it[NAME_FAVORITED] as Long
                val dataTemp = hashMapOf(NAME_FAVORITED to (favorited - 1))
                firebaseDatabase.collection(database).document(result.id.toString())
                    .set(dataTemp, SetOptions.merge())
            }
    }
}