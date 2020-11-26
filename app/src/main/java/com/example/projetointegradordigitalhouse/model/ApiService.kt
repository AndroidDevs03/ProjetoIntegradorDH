package com.example.projetointegradordigitalhouse.model

import com.example.digitalhousefoods_desafio2.extensions.md5
import com.example.projetointegradordigitalhouse.util.Constants.Api.API_HASH_NAME
import com.example.projetointegradordigitalhouse.util.Constants.Api.API_KEY_NAME
import com.example.projetointegradordigitalhouse.util.Constants.Api.API_TS_NAME
import com.example.projetointegradordigitalhouse.util.Constants.Api.BASE_URL
import com.example.projetointegradordigitalhouse.util.Constants.Api.PRIVATE_KEY
import com.example.projetointegradordigitalhouse.util.Constants.Api.PUBLIC_KEY
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiService {

    val marvelApi = getMarvelApiClient().create(MarvelApi::class.java)

    private fun getMarvelApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getInterceptorClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getInterceptorClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val interceptor = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)

//                Authentication for Server-Side Applications
//
//                    Server-side applications must pass two parameters in addition to the apikey parameter:
//
//                ts - a timestamp (or other long string which can change on a request-by-request basis)
//                hash - a md5 digest of the ts parameter, your private key and your public key (e.g. md5(ts+privateKey+publicKey)
//
//                For example, a user with a public key of "1234" and a private key of "abcd" could construct a valid call as follows:
//                http://gateway.marvel.com/v1/public/comics?ts=1&apikey=1234&hash=ffd275c5130566a2916217b101f26150 (the hash value is the md5 digest of 1abcd1234)

            .addInterceptor { chain ->
                var ts = System.currentTimeMillis()
                var hash = ("$ts$PRIVATE_KEY$PUBLIC_KEY").md5()
                val url = chain.request().url().newBuilder()
                    .addQueryParameter(API_TS_NAME, ts.toString())
                    .addQueryParameter(API_KEY_NAME, PUBLIC_KEY)
                    .addQueryParameter(API_HASH_NAME, hash)
                    .build()
                val newRequest = chain.request().newBuilder().url(url).build()
                chain.proceed(newRequest)
            }
        return interceptor.build()
    }

}