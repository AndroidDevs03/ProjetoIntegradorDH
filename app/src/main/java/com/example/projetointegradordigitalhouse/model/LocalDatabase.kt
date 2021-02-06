package com.example.projetointegradordigitalhouse.model

import android.content.Context
import androidx.room.*
import com.example.projetointegradordigitalhouse.util.Constants.Values.CONST_MAX_SEARCH_RESULTS

//tabela
@Entity(tableName = "searchResults")
data class Search(
    @PrimaryKey val busca: String,
    @ColumnInfo(name = "user") val user: Int = 0,
    @ColumnInfo(name = "date") val date: String
)

//métodos de acesso
@Dao
interface SearchDao {
    @Query("SELECT busca FROM searchResults ORDER BY date DESC LIMIT ${CONST_MAX_SEARCH_RESULTS}")
    suspend fun getLastSearchResults(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg users: Search)

    @Delete
    suspend fun delete(user: Search)

}

object LocalDatabase {
    @Database(entities = arrayOf(Search::class), version = 1,exportSchema = false)
    abstract class LocalRoomDatabase : RoomDatabase() {
        abstract fun userDao(): SearchDao
    }

    fun getDatabase(context: Context): LocalRoomDatabase {
        return Room.databaseBuilder(
            context,
            LocalRoomDatabase::class.java,
            "room_db"
        ).build()
    }
}



