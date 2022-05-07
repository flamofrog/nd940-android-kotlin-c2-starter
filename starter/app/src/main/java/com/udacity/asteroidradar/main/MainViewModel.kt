package com.udacity.asteroidradar.main

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

enum class ApiStatus { LOADING, ERROR, DONE }

class MainViewModel(
    private val database: AsteroidDatabaseDao
) : ViewModel() {

    private val _navigateToAsteroid = MutableLiveData<Asteroid?>()
    val navigateToAsteroid: LiveData<Asteroid?>
        get() = _navigateToAsteroid

    private val _asteroidApiStatus = MutableLiveData<ApiStatus>()
    val asteroidApiStatus: LiveData<ApiStatus>
        get() = _asteroidApiStatus

    private val _imageProperties = MutableLiveData<PictureOfDay?>()
    val imageProperties: LiveData<PictureOfDay?>
        get() = _imageProperties

    private val _asteroidList = MutableLiveData<ArrayList<Asteroid>>()
    val asteroidList: LiveData<ArrayList<Asteroid>>
        get() = _asteroidList

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToAsteroid.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToAsteroid.value = null
    }

    init {
        getImageOfTheDay()
        getAsteroids(AsteroidApiFilter.SHOW_WEEK)
    }
    private fun getStartAndEndDates(daysToInclude: Int): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val startDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, daysToInclude)
        val endDate = dateFormat.format(calendar.time)
        return Pair(startDate, endDate)
    }

    private fun getAsteroids(filter: AsteroidApiFilter) {
        fun loadAsteroidsFromDatabase() {
            _asteroidApiStatus.value = ApiStatus.LOADING
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
            val dateToday = dateFormat.format(calendar.time)
            viewModelScope.launch {
                _asteroidList.value = ArrayList(database.getAllSince(dateToday))
                _asteroidApiStatus.value = ApiStatus.DONE
            }
        }

        fun loadAsteroidsFromApi(startDate: String, endDate: String) {
            viewModelScope.launch {
                _asteroidApiStatus.value = ApiStatus.LOADING
                try {
                    val asteroidList = parseAsteroidsJsonResult(JSONObject(AsteroidApi.retrofitService.getAsteroidsList(startDate, endDate)))
                    database.insert(asteroidList)
                    _asteroidList.value = asteroidList
                    _asteroidApiStatus.value = ApiStatus.DONE
                } catch (e: Exception) {
                    Log.e("kento", "Failed to load Asteroids from the API: ${e.message}")
                    _asteroidApiStatus.value = ApiStatus.ERROR
                    _asteroidList.value = arrayListOf()
                }
            }
        }
        when (filter) {
            AsteroidApiFilter.SHOW_SAVED -> {
                loadAsteroidsFromDatabase()
            }
            AsteroidApiFilter.SHOW_WEEK -> {
                val dates = getStartAndEndDates(Constants.DEFAULT_END_DATE_DAYS)
                loadAsteroidsFromApi(dates.first, dates.second)
            }
            AsteroidApiFilter.SHOW_TODAY -> {
                val dates = getStartAndEndDates(1)
                loadAsteroidsFromApi(dates.first, dates.second)
            }
        }
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        getAsteroids(filter)
    }

    private fun getImageOfTheDay() {
        viewModelScope.launch {
            try {
                _imageProperties.value = AsteroidApi.retrofitService.getImageOfTheDay()
            } catch (e: Exception) {
                _imageProperties.value = null
            }
        }
    }

}