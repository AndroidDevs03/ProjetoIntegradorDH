package com.example.projetointegradordigitalhouse.model

import com.example.projetointegradordigitalhouse.model.characters.Characters
import retrofit2.Response
import retrofit2.http.GET

interface MarvelApi {
        @GET("characters?nameStartsWith=Spider-Man&orderBy=name&apikey=c32125ee0eec7c5e61969691516f131c")
        suspend fun Characters(): Response<Characters>
}