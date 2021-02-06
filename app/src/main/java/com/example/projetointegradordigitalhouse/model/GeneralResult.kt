package com.example.projetointegradordigitalhouse.model

import android.os.Parcelable
import com.example.projetointegradordigitalhouse.model.comics.Series
import kotlinx.android.parcel.Parcelize

@Parcelize
open class GeneralResult(
    // Common properties
    val id: Int,
    val name: String,
    val thumbnail: String,
    val description: String,
    var searchTagFlag: Boolean,
    var favoriteTagFlag: Boolean
) : Parcelable

@Parcelize
class CharacterResult(
    id: Int,
    name: String,
    thumbnail: String,
    description: String,
    searchTagFlag: Boolean,
    favoriteTagFlag: Boolean
) : GeneralResult(
    id,
    name,
    thumbnail,
    description,
    searchTagFlag,
    favoriteTagFlag
)

@Parcelize
class SeriesResult(
    id: Int,
    name: String,
    thumbnail: String,
    description: String,
    searchTagFlag: Boolean,
    favoriteTagFlag: Boolean,
    val charactersList: List<Int>,
    val comicsList: List<Int>
) : GeneralResult(
    id,
    name,
    thumbnail,
    description,
    searchTagFlag,
    favoriteTagFlag
)
@Parcelize
class ComicResult(
    id: Int,
    name: String,
    thumbnail: String,
    description: String,
    searchTagFlag: Boolean,
    favoriteTagFlag: Boolean,
    val charactersList: List<Int>,
    val pageCount: Int,
    val issueNumber: Int,
    val seriesID: Int,
    val published: String,
    val price: Double
) : GeneralResult(
    id,
    name,
    thumbnail,
    description,
    searchTagFlag,
    favoriteTagFlag
)