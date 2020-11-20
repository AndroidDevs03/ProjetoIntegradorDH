package com.example.projetointegradordigitalhouse.model

import com.example.projetointegradordigitalhouse.util.Constants.Api.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {

    val marvelApi = getMarvelApiClient().create(MarvelApi::class.java)

    private fun getMarvelApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}