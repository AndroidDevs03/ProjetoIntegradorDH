package com.example.projetointegradordigitalhouse.model

import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_CHARACTER_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_CHARACTER_LIST
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_COMIC_LIST
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_DESCRIPTION
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_FAVORITED
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_ISSUE_NUMBER
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_NAME
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_PAGE_COUNT
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_PRICE
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_PUBLISHED
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_SERIES_ID
import com.example.projetointegradordigitalhouse.util.Constants.Firebase.NAME_THUMBNAIL
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseFirestore {

    private val firebaseDatabase by lazy { Firebase.firestore }

    fun getMostPopularCharacters(): MutableList<CharacterResult>{
        val tempList = mutableListOf<CharacterResult>()
        firebaseDatabase.collection(NAME_CHARACTER_DATABASE).orderBy(NAME_FAVORITED).limit(5).get()
            .addOnSuccessListener { result ->
            for (document in result) {
                val tempData = CharacterResult(
                    document.id.toInt(),
                    document[NAME_NAME].toString(),
                    document[NAME_THUMBNAIL].toString(),
                    document[NAME_DESCRIPTION].toString(),
                    false,
                    false
                )
                tempList.add(tempData)
            }
        }
        return tempList
    }
    fun getMostPopularSeries(): MutableList<SeriesResult>{
        val tempList = mutableListOf<SeriesResult>()
        firebaseDatabase.collection(NAME_CHARACTER_DATABASE).orderBy(NAME_FAVORITED).limit(5).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tempData = SeriesResult(
                        document.id.toInt(),
                        document[NAME_NAME].toString(),
                        document[NAME_THUMBNAIL].toString(),
                        document[NAME_DESCRIPTION].toString(),
                        false,
                        false,
                        document[NAME_CHARACTER_LIST] as List<Int>,
                        document[NAME_COMIC_LIST] as List<Int>
                    )
                    tempList.add(tempData)
                }
            }
        return tempList
    }
    fun getMostPopularComics(): MutableList<ComicResult>{
        val tempList = mutableListOf<ComicResult>()
        firebaseDatabase.collection(NAME_CHARACTER_DATABASE).orderBy(NAME_FAVORITED).limit(5).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tempData = ComicResult(
                        document.id.toInt(),
                        document[NAME_NAME].toString(),
                        document[NAME_THUMBNAIL].toString(),
                        document[NAME_DESCRIPTION].toString(),
                        false,
                        false,
                        document[NAME_CHARACTER_LIST] as List<Int>,
                        document[NAME_PAGE_COUNT] as Int,
                        document[NAME_ISSUE_NUMBER] as Int,
                        document[NAME_SERIES_ID] as Int,
                        document[NAME_PUBLISHED] as String,
                        document[NAME_PRICE]  as Double
                    )
                    tempList.add(tempData)
                }
            }
        return tempList
    }
}