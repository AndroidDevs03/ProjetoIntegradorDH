package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.ComicResult
import com.example.projetointegradordigitalhouse.model.SeriesResult
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import kotlinx.coroutines.launch

class CharacterViewModel(context: Context): ViewModel() {

    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }

    var charSeriesList: MutableLiveData<List<SeriesResult>> = MutableLiveData()
    var charComicsList: MutableLiveData<List<ComicResult>> = MutableLiveData()

    fun getCharacterSeries(charSeriesID: List<Long>?) {
        var  charSeries = mutableListOf<SeriesResult>()

        Log.i("CharacterViewModel", "Series List")
        viewModelScope.launch {
            val allseries = repository.getAllSeries()

            allseries?.forEach {
                charSeriesID?.forEach { serieID ->
                    if(serieID == it.id){
                        charSeries.add(it)
//                        Log.i("CharacterViewModel", " ADD: ${it.id} ${it.name} Series Total - ${charSeries.size}")
                    }
                }
            }
            charSeriesList.postValue(charSeries.toList())
        }
    }
    fun getCharacterComics(charID: Long){
        var  charComics = mutableListOf<ComicResult>()

        Log.i("CharacterViewModel", "Comic List")
        viewModelScope.launch {
            val allcomics = repository.getAllComics()

            allcomics?.forEach { comic ->

                comic.charactersList?.let { listCharsID ->
                    listCharsID.forEach {
                        if (it == charID){
                            charComics.add(comic)
                            Log.i("CharacterViewModel", " ADD: ${comic.id} ${comic.name} Comics Total - ${charComics.size}")

                        }

                    }
                }
            }
            charComicsList.postValue(charComics.toList())
        }
    }
}