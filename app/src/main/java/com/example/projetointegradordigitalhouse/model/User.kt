package com.example.projetointegradordigitalhouse.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate.now
import java.time.LocalDate.parse
import java.time.LocalDateTime

@Parcelize
open class User() : Parcelable {
    lateinit var user_id: String
    var avatar_id: Int = 0
    var name: String = ""
    var email: String = ""
    var favorite_character_list: List<Long>? = null
    var favorite_series_list: MutableSet<FavoriteSeries>? = null
    var favorite_comic_list: MutableSet<FavoriteComic>? = null
    var lastUpdate: String = "TODO"

    constructor(
        id: String,
        avatarId: Int = 0,
        name: String = "",
        email: String = "",
        favoritesChars: List<Long>?,
        favoriteSeries: MutableSet<FavoriteSeries>?,
        favoriteComic: MutableSet<FavoriteComic>?,
        lastUpdate:String = "TODO"
    ):this(){
        this.user_id = id
        this.avatar_id = avatarId
        this.name = name
        this.email = email
        this.favorite_character_list = favoritesChars
        this.favorite_series_list = favoriteSeries
        this.favorite_comic_list = favoriteComic
        this.lastUpdate = lastUpdate
    }


    fun needUpdate(): Boolean {
        return LocalDateTime.parse(lastUpdate).isBefore(LocalDateTime.now().minusDays(2L))
    }
}

//@Parcelize
//class FavoriteItem(
//    val id: Int,
//    val name: String,
//    val thumbnail: String,
//    val description: String
//
//): Parcelable