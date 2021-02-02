package com.example.projetointegradordigitalhouse.model

import androidx.room.*

//tabela
@Entity(tableName = "searchResults")
data class Search (
    @PrimaryKey val searchId: Int,
    @ColumnInfo(name = "busca") val busca: String,
    @ColumnInfo(name = "user") val user: Int,
    @ColumnInfo(name = "date") val date: String
        )

//métodos de acesso
@Dao
interface SearchDao {
    @Query("SELECT * FROM searchResults")
    fun getAll(): List<Search>

    @Query("SELECT * FROM searchResults WHERE searchId IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Search>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Search

    @Insert
    fun insertAll(vararg users: Search)

    @Delete
    fun delete(user: Search)

}

@Database(entities = arrayOf(Search::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): SearchDao
}
