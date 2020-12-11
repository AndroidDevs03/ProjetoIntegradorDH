package com.example.projetointegradordigitalhouse.model.characters

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Thumbnail(
    val extension: String,
    val path: String
): Parcelable {
    fun getThumb():String{
        return "$path.$extension"
    }
}