package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.PictureOfDay
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.nasa.gov/"
const val API_KEY = "API_KEY_GOES_HERE"

enum class AsteroidApiFilter(val value: String) { SHOW_WEEK("week"), SHOW_TODAY("today"), SHOW_SAVED("saved") }

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface AsteroidApiService {
    @GET("planetary/apod")
    suspend fun getImageOfTheDay(@Query("api_key") api_key: String = API_KEY):
            PictureOfDay


    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidsList(@Query("start_date") start_date: String, @Query("end_date") end_date: String, @Query("api_key") api_key: String = API_KEY):
            String
}


object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}