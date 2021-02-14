package com.example.projetointegradordigitalhouse.model

import android.content.Context
import androidx.room.*
import com.example.projetointegradordigitalhouse.util.Constants.Values.CONST_MAX_SEARCH_HISTORY

//tabelas
@Entity(tableName = "searchResults")
data class Search(
    @PrimaryKey val busca: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "date") val date: String
)
@Entity(tableName = "favoriteCharacters")
data class FavoriteChar(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "thumbnail") val thumbnail: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "favorite") val favorite: Boolean
)
@Entity(tableName = "favoriteSeries")
data class FavoriteSeries(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "thumbnail") val thumbnail: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "favorite") val favorite: Boolean
)
@Entity(tableName = "favoriteComics")
data class FavoriteComic(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "thumbnail") val thumbnail: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "favorite") val favorite: Boolean
)

//métodos de acesso
@Dao
interface SearchDao {
    @Query("SELECT busca FROM searchResults ORDER BY date DESC LIMIT ${CONST_MAX_SEARCH_HISTORY}")
    suspend fun getLastSearchResults(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg users: Search)

    @Delete
    suspend fun delete(search: Search)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favoriteCharacters WHERE user_id = :userID")
    suspend fun getAllFavoriteCharacters(userID: String ): Array<FavoriteChar>
    @Query("SELECT * FROM favoriteSeries WHERE user_id = :userID")
    suspend fun getAllFavoriteSeries(userID: String ): Array<FavoriteSeries>
    @Query("SELECT * FROM favoriteComics WHERE user_id = :userID")
    suspend fun getAllFavoriteComics(userID: String ): Array<FavoriteComic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChar(vararg favorite: FavoriteChar)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(vararg favorite: FavoriteSeries)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComic(vararg favorite: FavoriteComic)

    @Delete
    suspend fun delete(vararg favorite: FavoriteChar)
    @Delete
    suspend fun delete(vararg favorite: FavoriteSeries)
    @Delete
    suspend fun delete(vararg favorite: FavoriteComic)
}

object LocalDatabase {
    @Database(entities = arrayOf(Search::class, FavoriteChar::class, FavoriteSeries::class, FavoriteComic::class), version = 1,exportSchema = false)
    abstract class LocalRoomDatabase : RoomDatabase() {
        abstract fun searchDao(): SearchDao
        abstract fun favoriteDao(): FavoriteDao
    }

    fun getDatabase(context: Context): LocalRoomDatabase {
        return Room.databaseBuilder(
            context,
            LocalRoomDatabase::class.java,
            "room_db"
        ).build()
    }
}



