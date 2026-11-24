package com.cuaca.daynight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuaca.daynight.data.model.AirQualityResponse
import com.cuaca.daynight.data.model.WeatherResponse
import com.cuaca.daynight.data.repository.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class WeatherUiState(
    val weather: WeatherResponse? = null,
    val airQuality: AirQualityResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdated: LocalDateTime? = null
)

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var lastLatitude = -7.7956
    private var lastLongitude = 110.3695

    fun fetchWeather(
        latitude: Double = -7.7956,
        longitude: Double = 110.3695
    ) {
        lastLatitude = latitude
        lastLongitude = longitude

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                // Fetch weather & air quality in parallel
                val weatherDeferred = async {
                    repository.getWeather(latitude = latitude, longitude = longitude)
                }
                val aqiDeferred = async {
                    try {
                        repository.getAirQuality(latitude = latitude, longitude = longitude)
                    } catch (_: Exception) {
                        null
                    }
                }

                val weatherResult = weatherDeferred.await()
                val aqiResult = aqiDeferred.await()

                _uiState.value = WeatherUiState(
                    weather = weatherResult,
                    airQuality = aqiResult,
                    lastUpdated = LocalDateTime.now()
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal mengambil data cuaca"
                )
            }
        }
    }

    fun refreshWeather() {
        fetchWeather(
            latitude = lastLatitude,
            longitude = lastLongitude
        )
    }
}
