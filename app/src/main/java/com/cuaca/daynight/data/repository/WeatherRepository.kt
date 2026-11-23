package com.cuaca.daynight.data.repository

import com.cuaca.daynight.data.model.WeatherResponse
import com.cuaca.daynight.data.remote.RetrofitClient

class WeatherRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): WeatherResponse {
        return apiService.getWeather(
            latitude = latitude,
            longitude = longitude
        )
    }
}
