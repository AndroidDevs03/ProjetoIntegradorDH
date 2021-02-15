package com.example.projetointegradordigitalhouse.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate.now
import java.time.LocalDate.parse

@Parcelize
open class User(

    val id: String,
    var avatarId: Number,
    var name: String,
    var email: String,
    var favoritesItens: UserFavorites? = null,
    var lastUpdate: String = "TODO"
) : Parcelable {
    fun needUpdate(): Boolean {
        return parse(lastUpdate).isBefore(now().minusDays(2L))
    }

}

@Parcelize
class UserFavorites(
    var sCharacters: MutableSet<CharacterResult>? = null,
    var sComics: MutableSet<ComicResult>? = null,
    var sSeries: MutableSet<SeriesResult>? = null
): Parcelable