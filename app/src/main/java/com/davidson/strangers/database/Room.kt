package com.davidson.strangers.database

import android.content.Context
import androidx.room.*

@Dao
interface StrangerDao {

    @Query("SELECT * FROM databaseStranger")
    fun getAllStrangerFromDb(): List<DatabaseStranger>

    @Query("SELECT * FROM databaseStranger WHERE strangerId = :strangerId")
    fun getStrangerFromDb(strangerId: Long): DatabaseStranger

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(strangers: List<DatabaseStranger>)

    @Query("SELECT * FROM databaseStranger WHERE strangerName like '%' ||:searchName || '%'")
    fun search(searchName: String): List<DatabaseStranger>

    @Query("DELETE FROM databaseStranger")
    fun deleteAll()

    @Query("DELETE from sqlite_sequence WHERE name = 'databaseBreakingBad'")
    fun resetAutoInc()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertOne(stranger: DatabaseStranger)
}

@Database(entities = [DatabaseStranger::class], version = 1, exportSchema = false)
abstract class StrangerDatabase : RoomDatabase() {
    abstract val strangerDao: StrangerDao
}

private lateinit var INSTANCE: StrangerDatabase

fun getDatabase(context: Context): StrangerDatabase {
    synchronized(StrangerDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                StrangerDatabase::class.java,
                "stranger"
            ).build()
        }
    }
    return INSTANCE
}