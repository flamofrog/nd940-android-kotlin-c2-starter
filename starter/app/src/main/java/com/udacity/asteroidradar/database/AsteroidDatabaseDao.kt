package com.udacity.asteroidradar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid
import java.util.*

@Dao
interface AsteroidDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asteroid: List<Asteroid>)

    @Query("SELECT * FROM asteroid_table WHERE close_approach_date >= :fromDate ORDER BY close_approach_date ASC")
    suspend fun getAllSince(fromDate: String): List<Asteroid>

    @Query("SELECT * FROM asteroid_table WHERE close_approach_date BETWEEN :fromDate AND :toDate ORDER BY close_approach_date ASC")
    suspend fun getAllBetween(fromDate: String, toDate: String): List<Asteroid>

}