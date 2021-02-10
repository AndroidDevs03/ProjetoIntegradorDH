package com.example.projetointegradordigitalhouse.model.characters

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemXX(
    val name: String,
    val resourceURI: String
): Parcelable