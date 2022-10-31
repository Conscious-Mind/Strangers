package com.davidson.strangers.network

import com.davidson.strangers.test.TestCase
import com.davidson.strangers.test.WeatherTest
import com.davidson.strangers.util.Constants.Companion.API_WEATHER_KEY_2
import com.davidson.strangers.util.Constants.Companion.BASE_URL_RANDOM_USER_API
import com.davidson.strangers.util.Constants.Companion.BASE_URL_WEATHERBIT_API
import com.davidson.strangers.util.Constants.Companion.NUMBER_OF_USER_TO_FETCH
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_RANDOM_USER_API)
    .build()

private val retrofitWeather = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_WEATHERBIT_API)
    .build()

interface RandomUserService {
    @GET("api")
    suspend fun getAllRandomUserFromNetwork(
        @Query("results")
        numberOfUserToFetch: Int = NUMBER_OF_USER_TO_FETCH,
    ): TestCase
}


interface WeatherService {
    @GET("v2.0/current")
    suspend fun getWeatherFromNetwork(
        @Query("lat")
        latitude: Double,
        @Query("lon")
        longitude: Double,
        @Query("key")
        apiKey: String = API_WEATHER_KEY_2
    ): WeatherTest
}

object RandomUserNetwork {
    val retrofitService: RandomUserService by lazy { retrofit.create(RandomUserService::class.java) }
}

object WeatherNetwork {
    val retrofitWeatherService: WeatherService by lazy { retrofitWeather.create(WeatherService::class.java) }
}