package com.example.projetointegradordigitalhouse.model

import com.example.projetointegradordigitalhouse.model.characters.Characters
import com.example.projetointegradordigitalhouse.model.characters.Comics
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelApi {
        //Spider-Man            ID 1009610
        //Wolverine             ID 1009718
        //Captain-America       ID 1009220

        @GET("characters")
        suspend fun CharactersByName(@Query("nameStartsWith")charName: String,@Query("limit")limit: Int = 10,@Query("offset")offset: Int = 0): Response<Characters>

        @GET("characters/{characterId}")
        suspend fun CharactersByID(@Path("characterId") charID: Int): Response<Characters>

        @GET("comics")
        suspend fun ComicsByCharsID(@Query("characters")charID: List<Int>): Response<Comics>

}