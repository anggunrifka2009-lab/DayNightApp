package com.cuaca.daynight.data.repository

import com.cuaca.daynight.data.model.AirQualityResponse
import com.cuaca.daynight.data.model.WeatherResponse
import com.cuaca.daynight.data.remote.RetrofitClient

class WeatherRepository {

    private val apiService = RetrofitClient.apiService
    private val airQualityApiService = RetrofitClient.airQualityApiService

    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): WeatherResponse {
        return apiService.getWeather(
            latitude = latitude,
            longitude = longitude
        )
    }

    suspend fun getAirQuality(
        latitude: Double,
        longitude: Double
    ): AirQualityResponse {
        return airQualityApiService.getAirQuality(
            latitude = latitude,
            longitude = longitude
        )
    }
}
