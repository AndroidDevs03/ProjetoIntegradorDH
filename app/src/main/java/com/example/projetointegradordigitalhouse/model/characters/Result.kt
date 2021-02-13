package com.example.projetointegradordigitalhouse.model.characters

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Result(

    var description: String,
    val id: Int,
    val modified: String,
    val name: String,
    val resourceURI: String,
    val series: Series,
    val thumbnail: Thumbnail

    //val comics: Comics,
    //val events: Events,
    // val stories: Stories,
    // val urls: List<Url>
): Parcelable