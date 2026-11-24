package com.cuaca.daynight.data.remote

import com.cuaca.daynight.data.model.AirQualityResponse
import com.cuaca.daynight.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,

        @Query("current") current: String =
            "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code," +
            "wind_speed_10m,wind_direction_10m,surface_pressure,uv_index,wind_gusts_10m,dew_point_2m",

        @Query("hourly") hourly: String =
            "temperature_2m,precipitation_probability,weather_code,wind_speed_10m,precipitation",

        @Query("daily") daily: String =
            "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset," +
            "uv_index_max,daylight_duration,sunshine_duration",

        @Query("timezone") timezone: String = "auto",

        @Query("forecast_days") forecastDays: Int = 7
    ): WeatherResponse
}

interface AirQualityApiService {

    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "us_aqi,pm2_5,pm10",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 1
    ): AirQualityResponse
}
