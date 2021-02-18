package com.example.projetointegradordigitalhouse.model

import com.example.digitalhousefoods_desafio2.extensions.md5
import com.example.projetointegradordigitalhouse.model.characters.CharacterResponse
import com.example.projetointegradordigitalhouse.model.comics.ComicResponse
import com.example.projetointegradordigitalhouse.model.series.SeriesResponse
import com.example.projetointegradordigitalhouse.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object MarvelApi {

        val commands = getMarvelApiClient().create(MarvelApiQueries::class.java)
        private fun getMarvelApiClient(): Retrofit {
                return Retrofit.Builder()
                        .baseUrl(Constants.Api.BASE_MARVEL_URL)
                        .client(getMarvelInterceptorClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
        }
        private fun getMarvelInterceptorClient(): OkHttpClient {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                val interceptor = OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(loggingInterceptor)

//                Authentication for Server-Side Applications
//
//                Server-side applications must pass two parameters in addition to the apikey parameter:
//
//                ts - a timestamp (or other long string which can change on a request-by-request basis)
//                hash - a md5 digest of the ts parameter, your private key and your public key (e.g. md5(ts+privateKey+publicKey)
//
//                For example, a user with a public key of "1234" and a private key of "abcd" could construct a valid call as follows:
//                http://gateway.marvel.com/v1/public/comics?ts=1&apikey=1234&hash=ffd275c5130566a2916217b101f26150 (the hash value is the md5 digest of 1abcd1234)

                        .addInterceptor { chain ->
                                val ts = System.currentTimeMillis()
                                val hash = ("$ts${Constants.Api.MARVEL_KEY}${Constants.Api.PUBLIC_KEY}").md5()
                                val url = chain.request().url().newBuilder()
                                        .addQueryParameter(Constants.Api.API_TS_NAME, ts.toString())
                                        .addQueryParameter(
                                                Constants.Api.API_KEY_NAME,
                                                Constants.Api.PUBLIC_KEY
                                        )
                                        .addQueryParameter(Constants.Api.API_HASH_NAME, hash)
                                        .build()
                                val newRequest = chain.request().newBuilder().url(url).build()
                                chain.proceed(newRequest)
                        }
                return interceptor.build()
        }
}


interface MarvelApiQueries {

        @GET("characters")
        suspend fun charactersByName(@Query("nameStartsWith")charName: String, @Query("limit")limit: Int = 20, @Query("offset")offset: Int = 0): Response<CharacterResponse>

        @GET("series")
        suspend fun seriesByName(@Query("titleStartsWith")charName: String, @Query("limit")limit: Int = 10, @Query("offset")offset: Int = 0): Response<SeriesResponse>

        @GET("comics")
        suspend fun comicsByName(@Query("titleStartsWith")charName: String, @Query("limit")limit: Int = 10, @Query("offset")offset: Int = 0): Response<ComicResponse>

        @GET("characters/{characterId}")
        suspend fun charactersByID(@Path("characterId") charID: Long): Response<CharacterResponse>

        //https://gateway.marvel.com:443/v1/public/characters/1009664/series?orderBy=-modified&limit=100&apikey=c32125ee0eec7c5e61969691516f131c
        @GET("characters/{characterId}/series")
        suspend fun seriesByCharacterID(@Path("characterId") charID: Long,  @Query("limit")limit: Int = 40, @Query("orderBy")orderBy: String = "-modified"): Response<SeriesResponse>

        @GET("comics/{comicId}")
        suspend fun comicsBySeriesID(@Path("comicId")comicID: Long): Response<ComicResponse>

}