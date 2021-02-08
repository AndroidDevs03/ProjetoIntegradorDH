package com.example.projetointegradordigitalhouse.model

import android.os.Parcelable
import com.example.projetointegradordigitalhouse.model.comics.Series
import kotlinx.android.parcel.Parcelize

@Parcelize
open class GeneralResult(
    // Common properties
    open val id: Int,
    open val name: String,
    open val thumbnail: String,
    open val description: String,
    open var searchTagFlag: Boolean,
    open var favoriteTagFlag: Boolean,
    open var lastUpdate: String
) : Parcelable

@Parcelize
class CharacterResult(
    override val id: Int,
    override val name: String,
    override val thumbnail: String,
    override val description: String,
    override var searchTagFlag: Boolean,
    override var favoriteTagFlag: Boolean,
    override var lastUpdate: String
) : GeneralResult(
    id,
    name,
    thumbnail,
    description,
    searchTagFlag,
    favoriteTagFlag,
    lastUpdate
)

@Parcelize
class SeriesResult(
    override val id: Int,
    override val name: String,
    override val thumbnail: String,
    override val description: String,
    override var searchTagFlag: Boolean,
    override var favoriteTagFlag: Boolean,
    override var lastUpdate: String,
    val charactersList: List<Int>,
    val comicsList: List<Int>
) : GeneralResult(
    id,
    name,
    thumbnail,
    description,
    searchTagFlag,
    favoriteTagFlag,
    lastUpdate
)
@Parcelize
class ComicResult(
    override val id: Int,
    override val name: String,
    override val thumbnail: String,
    override val description: String,
    override var searchTagFlag: Boolean,
    override var favoriteTagFlag: Boolean,
    override var lastUpdate: String,
    val charactersList: List<Int>,
    val pageCount: String,
    val issueNumber: String,
    val seriesID: Long,
    val published: String,
    val price: Double
) : GeneralResult(
    id,
    name,
    thumbnail,
    description,
    searchTagFlag,
    favoriteTagFlag,
    lastUpdate
)