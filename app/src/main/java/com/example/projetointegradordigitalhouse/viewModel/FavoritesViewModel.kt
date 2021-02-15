package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.*
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class FavoritesViewModel(context: Context): ViewModel() {

    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }
//    private val charactersLocalDb: FavoriteDao by lazy { LocalDatabase.getDatabase(context).favoriteDao() }
//    private val sharedPreferences: MarvelXSharedPreferences by lazy { MarvelXSharedPreferences(context) }
//    private val firebaseAuth by lazy { Firebase.auth }
    var favoriteCharList: MutableLiveData<List<FavoriteChar>> = MutableLiveData()
    var favoriteSeriesList: MutableLiveData<Set<FavoriteSeries>> = MutableLiveData()
    var favoriteComicsList: MutableLiveData<Set<FavoriteComic>> = MutableLiveData()

//    fun getAllFavorite(userID: String){
//        Log.i("FavoritesViewModel", "All Favorites")
//        viewModelScope.launch {
//            val temp = repository.getAllFavorites(userID)
//        }
//    }
    fun getFavoritesCharacters(userID: String){
        Log.i("FavoritesViewModel", "Characters")
        viewModelScope.launch {
            val temp = repository.getFavoritesCharacters(userID)
//            temp.forEach {
//                Log.i("Favoritos Character: ", "${it.id} ${it.name}")
//            }
            favoriteCharList.postValue(temp)

        }
    }
    fun getFavoritesSeries(userID: String){
        Log.i("FavoritesViewModel", "Series")
        viewModelScope.launch {
            val temp = repository.getFavoritesSeries(userID)
            temp.forEach {
                Log.i("Favoritos Series: ", "${it.id} ${it.name}")
            }
            favoriteSeriesList.postValue(temp)

        }
    }
    fun getFavoritesComics(userID: String){
        Log.i("FavoritesViewModel", "Comics")
        viewModelScope.launch {
            val temp = repository.getFavoritesComics(userID)
            temp.forEach {
                Log.i("Favoritos Comics: ", "${it.id} ${it.name}")
            }
            favoriteComicsList.postValue(temp)

        }
    }
}