package com.example.projetointegradordigitalhouse.util

class Constants {

    object Api {
        const val BASE_MARVEL_URL = "https://gateway.marvel.com:443/v1/public/"
        const val BASE_TMDB_URL = "https://api.themoviedb.org/3"
        const val TMDB_KEY = "dc3e4c789ef7023024d652afab6f2858"
        //public Roberto c32125ee0eec7c5e61969691516f131c
        //public Jonatas 293a2b82340c11ba84ef61de4104f190
        const val PUBLIC_KEY = "293a2b82340c11ba84ef61de4104f190"
        //Private Roberto a06ba3db59e199db547c0cc9a65e173acf77419e
        //Private Jonatas 9ac20bfb6075dd43cf17221a49c974478e87ded4
        const val MARVEL_KEY = "9ac20bfb6075dd43cf17221a49c974478e87ded4"
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 1
        const val API_TS_NAME = "ts"
        const val API_HASH_NAME = "hash"
        const val BASE_URL_ORIGINAL_IMAGE = "http://i.annihil.us/u/prod/marvel/i/mg/3/50/"
        const val API_KEY_NAME = "apikey"
        const val API_KEY_VALUE = "c32125ee0eec7c5e61969691516f131c"
        const val API_CONTENT_TYPE_NAME = "Content-Type"
        const val API_CONTENT_TYPE_VALUE = "application/json;charset=utf-8"
        const val QUERY_PARAM_LANGUAGE_LABEL = "language"
        const val QUERY_PARAM_LANGUAGE_VALUE = "pt-BR"
        const val KEY_ACTIVE_SEARCH = "activeSearch"
    }

    object Intent {
        const val KEY_INTENT_SEARCH = "search"
        const val KEY_INTENT_DATA = "data"
    }

}