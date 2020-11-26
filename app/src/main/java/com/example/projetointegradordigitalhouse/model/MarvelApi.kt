package com.example.projetointegradordigitalhouse.model

import com.example.projetointegradordigitalhouse.model.characters.Characters
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MarvelApi {
        @GET("characters?nameStartsWith=Spider-Man&orderBy=name&apikey=c32125ee0eec7c5e61969691516f131c")
        suspend fun Characters(@Query("page")pagenumber: Int): Response<Characters>
}