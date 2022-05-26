package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.utils.DateUtils.Companion.getStartAndEndDates
import retrofit2.HttpException

class RefreshAsteroidsWork(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshAsteroidsWork"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getInstance(applicationContext)
        val repository = AsteroidRepository(database)

        return try {
            val dates = getStartAndEndDates(1)
            repository.refreshAsteroids(dates.first, dates.second)
            Result.success()
        } catch (exception: Exception) {
            Result.retry()
        }
    }
}