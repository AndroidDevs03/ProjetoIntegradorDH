package com.example.projetointegradordigitalhouse.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
open class GeneralResult(
    // Common properties
    internal open val id: Long,
    open val name: String,
    open val thumbnail: String,
    open val description: String,
    open var searchTagFlag: Boolean,
    open var favoriteTagFlag: Boolean,
    open var lastUpdate: String
) : Parcelable {
    fun needUpdate(): Boolean {
        return LocalDateTime.parse(lastUpdate).isBefore(LocalDateTime.now().minusDays(2L))
    }
    fun checkSearchTag(searchTagList: MutableSet<String>){
        searchTagFlag = searchTagList.contains(id)
    }
    fun checkFavoriteTag(favoriteTagList: MutableList<Long>){
        favoriteTagFlag = favoriteTagList.contains(id)
    }
}

@Parcelize
class CharacterResult(
    override val id: Long,
    override val name: String,
    override val thumbnail: String,
    override val description: String,
    override var searchTagFlag: Boolean,
    override var favoriteTagFlag: Boolean,
    override var lastUpdate: String,
    val series: List<Long>
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
    override val id: Long,
    override val name: String,
    override val thumbnail: String,
    override val description: String,
    override var searchTagFlag: Boolean,
    override var favoriteTagFlag: Boolean,
    override var lastUpdate: String,
    val charactersList: List<Long>,
    val comicsList: List<Long>
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
    override val id: Long,
    override val name: String,
    override val thumbnail: String,
    override val description: String,
    override var searchTagFlag: Boolean,
    override var favoriteTagFlag: Boolean,
    override var lastUpdate: String,
    val charactersList: List<Long>,
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