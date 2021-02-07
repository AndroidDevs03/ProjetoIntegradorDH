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

    object Values {
        const val CONST_MAX_SEARCH_RESULTS = 4
    }

    object FirebaseNames {
        const val NAME_CHARACTER_DATABASE = "characters"
        const val NAME_SERIES_DATABASE = "series"
        const val NAME_COMICS_DATABASE = "comics"
        const val NAME_SEARCHES_DATABASE = "searchtags"
        const val NAME_USERS_DATABASE = "users"
        const val NAME_FAVORITED = "favorited"
        const val NAME_NAME = "name"
        const val NAME_THUMBNAIL = "thumbnail"
        const val NAME_DESCRIPTION = "description"
        const val NAME_CHARACTER_LIST = "characters"
        const val NAME_COMIC_LIST = "comics"
        const val NAME_ISSUE_NUMBER = "issueNumber"
        const val NAME_PAGE_COUNT = "pageCount"
        const val NAME_PUBLISHED = "published"
        const val NAME_SERIES_ID = "series"
        const val NAME_PRICE = "price"

    }
    object SharedPreferences {
        const val NAME_SP_DBNAME = "sp_database"
        const val NAME_SP_CURRENT_SEARCH= "current_search"
    }
}