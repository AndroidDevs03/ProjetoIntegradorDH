package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.CharacterResult
import com.example.projetointegradordigitalhouse.model.ComicResult
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import kotlinx.coroutines.launch

class SeriesViewModel(context: Context):ViewModel() {


    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }

    var seriesCharsList: MutableLiveData<List<CharacterResult>> = MutableLiveData()
    var seriesComicsList: MutableLiveData<List<ComicResult>> = MutableLiveData()



    fun getSeriesCharacters(charactersListID: List<Long>) {
        var  serieChars = mutableListOf<CharacterResult>()

        Log.i("SeriesViewModel", "Character List")
        viewModelScope.launch {
            val allCharacters = repository.getAllChars()

            allCharacters?.forEach {
                charactersListID?.forEach { charID ->
                    if(charID == it.id){
                        serieChars.add(it)
//                        Log.i("SeriesViewModel", " ADD: ${it.id} ${it.name} Chars Total - ${serieChars.size}")
                    }
                }
            }
            seriesCharsList.postValue(serieChars.toList())
        }
    }

    fun getSeriesComics(comicListID: List<Long>){
        var  seriesComics = mutableListOf<ComicResult>()

        Log.i("SeriesViewModel", "Comic List")
        viewModelScope.launch {
            val allcomics = repository.getAllComics()

            // Checagem de comics
            allcomics?.forEach {
                comicListID?.forEach { comicID ->
                    if (comicID == it.id){
                        seriesComics.add(it)
                        Log.i("SeriesViewModel", " ADD: ${it.id} ${it.name} Comics Total - ${seriesComics.size}")

                    }
                }
            }
            seriesComicsList.postValue(seriesComics.toList())

        }
        Log.i("SeriesViewModel", " Comics Total - ${seriesComics.size}")

    }

}