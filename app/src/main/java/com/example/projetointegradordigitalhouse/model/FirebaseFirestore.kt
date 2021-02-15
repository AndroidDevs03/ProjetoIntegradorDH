package com.example.projetointegradordigitalhouse.model

import android.util.Log
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_AVATAR
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_CHARACTER_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_CHARACTER_LIST
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_COMICS_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_COMIC_LIST
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_DESCRIPTION
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_EMAIL
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_FAVORITED
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_FAVORITE_CHARACTER_LIST
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_FAVORITE_COMIC_LIST
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_FAVORITE_SERIES_LIST
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_ISSUE_NUMBER
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_LAST_UPDATE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_NAME
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_PAGE_COUNT
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_PRICE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_PUBLISHED
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_SEARCHES_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_SERIES_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_SERIES_ID
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_THUMBNAIL
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_USERS_DATABASE
import com.example.projetointegradordigitalhouse.util.Constants.FirebaseNames.NAME_USER_ID
import com.example.projetointegradordigitalhouse.util.Constants.Values.CONST_DAYS_TO_UPDATE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
@Suppress("UNCHECKED_CAST")
class FirebaseFirestore {

    private val firebaseDatabase by lazy { Firebase.firestore }
    private val firebaseAuth by lazy { Firebase.auth }

    suspend fun getMostPopularCharacters(limit: Long): MutableList<CharacterResult> = suspendCoroutine{ ret ->
        val tempList = mutableListOf<CharacterResult>()
        firebaseDatabase.collection(NAME_CHARACTER_DATABASE).orderBy(NAME_FAVORITED).limit(limit).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tempData = CharacterResult(
                        document.id.toInt(),
                        document[NAME_NAME].toString(),
                        document[NAME_THUMBNAIL].toString(),
                        document[NAME_DESCRIPTION].toString(),
                        false,
                        false,
                        document[NAME_LAST_UPDATE].toString(),
                        document[NAME_SERIES_ID] as List<Long>
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
                        document[NAME_CHARACTER_LIST] as List<Long>,
                        document[NAME_COMIC_LIST] as List<Long>
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
                        document[NAME_CHARACTER_LIST] as List<Long>,
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
        dataTemp[NAME_SERIES_ID] = result.series
        dataTemp[NAME_FAVORITED] = 0L

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
        dataTemp[NAME_FAVORITED] = 0L

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
        dataTemp[NAME_FAVORITED] = 0L

        firebaseDatabase.collection(NAME_COMICS_DATABASE).document(result.id.toString())
            .set(dataTemp, SetOptions.merge())
    }
    fun insertSearchTag(tag: String) {
        val dataTemp: HashMap<String, Any> = HashMap()
        dataTemp[NAME_LAST_UPDATE] = LocalDateTime.now().toString()
        firebaseDatabase.collection(NAME_SEARCHES_DATABASE).document(tag)
            .set(dataTemp, SetOptions.merge())
    }
    fun incrementFavorited(resultID: Int, tabPosition: Int) {
        val database = when (tabPosition) {
            0 -> NAME_CHARACTER_DATABASE
            1 -> NAME_SERIES_DATABASE
            2 -> NAME_COMICS_DATABASE
            else -> ""
        }
        firebaseDatabase.collection(database).document(resultID.toString()).get()
            .addOnSuccessListener {
                val favorited = (it[NAME_FAVORITED]?: 0L) as Long
                val dataTemp = hashMapOf(NAME_FAVORITED to (favorited + 1))
                firebaseDatabase.collection(database).document(resultID.toString())
                    .set(dataTemp, SetOptions.merge())
            }
    }
    fun decrementFavorited(resultID: Int, tabPosition: Int) {
        val database = when (tabPosition) {
            0 -> NAME_CHARACTER_DATABASE
            1 -> NAME_SERIES_DATABASE
            2 -> NAME_COMICS_DATABASE
            else -> ""
        }
        firebaseDatabase.collection(database).document(resultID.toString()).get()
            .addOnSuccessListener {
                val favorited = (it[NAME_FAVORITED]?: 0L) as Long
                val dataTemp = hashMapOf(NAME_FAVORITED to (favorited - 1))
                firebaseDatabase.collection(database).document(resultID.toString())
                    .set(dataTemp, SetOptions.merge())
            }
    }
    suspend fun lastSearchNeedsUpdate(tag: String): Boolean = suspendCoroutine{ ret ->
        firebaseDatabase.collection(NAME_SEARCHES_DATABASE).document(tag).get()
            .addOnSuccessListener { result ->
                result[NAME_LAST_UPDATE]?.let {
                    Log.i("Firebase", "Busca '$tag' encontrada.")
                    ret.resume(
                        LocalDateTime.parse(result[NAME_LAST_UPDATE].toString())
                            .isBefore(LocalDateTime.now().minusDays(CONST_DAYS_TO_UPDATE))
                    )
                }?: run{
                    Log.i("Firebase", "Busca '$tag' não encontrada.")
                    ret.resume(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.i("Firebase", "Erro ao receber dados do Firebase. ",exception)
            }
    }
    suspend fun getAllChars(): MutableList<CharacterResult> = suspendCoroutine{ ret ->
        val tempList = mutableListOf<CharacterResult>()
        firebaseDatabase.collection(NAME_CHARACTER_DATABASE).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tempData = CharacterResult(
                        document.id.toInt(),
                        document[NAME_NAME].toString(),
                        document[NAME_THUMBNAIL].toString(),
                        document[NAME_DESCRIPTION].toString(),
                        false,
                        false,
                        document[NAME_LAST_UPDATE].toString(),
                        document[NAME_SERIES_ID] as List<Long>
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
    suspend fun getAllSeries(): MutableList<SeriesResult> = suspendCoroutine{ ret ->
        val tempList = mutableListOf<SeriesResult>()
        firebaseDatabase.collection(NAME_SERIES_DATABASE).get()
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
                        document[NAME_CHARACTER_LIST] as List<Long>,
                        document[NAME_COMIC_LIST] as List<Long>
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
    suspend fun getAllComics(): MutableList<ComicResult> = suspendCoroutine{ ret ->
        val tempList = mutableListOf<ComicResult>()
        firebaseDatabase.collection(NAME_COMICS_DATABASE).get()
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
                        document[NAME_CHARACTER_LIST] as List<Long>,
                        document[NAME_PAGE_COUNT].toString(),
                        document[NAME_ISSUE_NUMBER].toString(),
                        document[NAME_SERIES_ID] as Long,
                        document[NAME_PUBLISHED].toString(),
                        document[NAME_PRICE] as Double
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
    suspend fun updateFavoriteList(newFavorite: List<Int>, tabPosition: Int) {
        val dataTemp: HashMap<String, Any> = HashMap()
        val type = when (tabPosition) {
            0 -> NAME_FAVORITE_CHARACTER_LIST
            1 -> NAME_FAVORITE_SERIES_LIST
            2 -> NAME_FAVORITE_COMIC_LIST
            else -> ""
        }
        dataTemp[type] = newFavorite
        firebaseAuth.currentUser?.let {
            firebaseDatabase.collection(NAME_USERS_DATABASE).document(it.uid).set(dataTemp, SetOptions.merge())
        }
    }
    suspend fun insertUser(user: User){

        val dataTemp: HashMap<String, Any> = HashMap()
        dataTemp[NAME_USER_ID] = user.user_id
        dataTemp[NAME_AVATAR] = user.avatar_id
        dataTemp[NAME_NAME] = user.name
        dataTemp[NAME_EMAIL]= user.email
        dataTemp[NAME_FAVORITE_CHARACTER_LIST] = ""
        dataTemp[NAME_FAVORITE_SERIES_LIST] = ""
        dataTemp[NAME_FAVORITE_COMIC_LIST] = ""
        firebaseDatabase.collection(NAME_USERS_DATABASE)
//            .document(user.id)
            .document(dataTemp[NAME_USER_ID].toString())
            .set(dataTemp, SetOptions.merge())
        Log.i("Firebase", "Usuário registrado. ")

    }
    suspend fun getFavoritesCharacters(userID: String): List<Long>?{
        Log.i("FavoritesFirestore", "Characters 1")

        val tempSet:  List<Long>? = null
        Log.i("FavoritesFirestore", "Characters 2")

        firebaseAuth?.let{ firebase ->
            Log.i("FavoritesFirestore", "Characters 3")
            firebaseDatabase.collection(NAME_USERS_DATABASE)
                .document(firebase.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { result ->
                    Log.i("FavoritesFirestore", "Characters 4")

                    result?.let{
                        Log.i("FavoritesFirestore", "Characters 5")

                        val temp = it?.toObject<User>()
                        Log.i("FavoritesFirestore", "Characters 6")

                        temp?.favorite_character_list?.forEach { item->
                            Log.i("FavoritesFirestore", "Characters: ${item}")
                        }
                    }
                    Log.i("FavoritesFirestore", "Characters 7")


                }
                .addOnFailureListener {
                    Log.i("Firebase Characters", " Falha na recuperação")
                }
        }
        return tempSet

    }

    fun getCharById(resultID: Int): Any {
        return ""
    }
}