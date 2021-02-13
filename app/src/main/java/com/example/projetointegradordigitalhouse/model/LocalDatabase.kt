package com.example.projetointegradordigitalhouse.model

import android.content.Context
import androidx.room.*
import com.example.projetointegradordigitalhouse.util.Constants.Values.CONST_MAX_SEARCH_RESULTS

//tabelas
@Entity(tableName = "searchResults")
data class Search(
    @PrimaryKey val busca: String,
    @ColumnInfo(name = "user") val user: String,
    @ColumnInfo(name = "date") val date: String
)
@Entity(tableName = "userFavorites")
data class Favorite(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user") val user: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "thumbnail") val thumbnail: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "favorite") val favorite: Boolean
)

//métodos de acesso
@Dao
interface SearchDao {
    @Query("SELECT busca FROM searchResults ORDER BY date DESC LIMIT ${CONST_MAX_SEARCH_RESULTS}")
    suspend fun getLastSearchResults(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg users: Search)

    @Delete
    suspend fun delete(search: Search)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM userFavorites WHERE type = :typeResult" ) // + "AND user = :userID"
    suspend fun getAllFavorites(typeResult: String): Array<Favorite> // userID: String,

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Update
    suspend fun update(vararg favorite: Favorite)

}

object LocalDatabase {
    @Database(entities = arrayOf(Search::class, Favorite::class), version = 1,exportSchema = false)
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



