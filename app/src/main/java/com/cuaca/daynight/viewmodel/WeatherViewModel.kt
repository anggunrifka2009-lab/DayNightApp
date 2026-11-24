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
import kotlinx.coroutines.flow.update
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

    private val _weatherStates = MutableStateFlow<Map<String, WeatherUiState>>(emptyMap())
    val weatherStates: StateFlow<Map<String, WeatherUiState>> = _weatherStates.asStateFlow()

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun fetchWeather(
        cityName: String,
        latitude: Double,
        longitude: Double,
        forceRefresh: Boolean = false
    ) {
        val currentState = _weatherStates.value[cityName]
        if (!forceRefresh && currentState?.weather != null && !currentState.isLoading) {
            return
        }

        viewModelScope.launch {
            _weatherStates.update { currentMap ->
                val existing = currentMap[cityName] ?: WeatherUiState()
                currentMap + (cityName to existing.copy(isLoading = true, error = null))
            }

            try {
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

                val newState = WeatherUiState(
                    weather = weatherResult,
                    airQuality = aqiResult,
                    isLoading = false,
                    error = null,
                    lastUpdated = LocalDateTime.now()
                )

                _weatherStates.update { currentMap ->
                    currentMap + (cityName to newState)
                }

                _uiState.value = newState
            } catch (e: Exception) {
                val errState = (currentState ?: WeatherUiState()).copy(
                    isLoading = false,
                    error = e.message ?: "Gagal mengambil data cuaca"
                )
                _weatherStates.update { currentMap ->
                    currentMap + (cityName to errState)
                }
                _uiState.value = errState
            }
        }
    }

    fun fetchWeather(latitude: Double = -7.7956, longitude: Double = 110.3695) {
        fetchWeather("Default", latitude, longitude)
    }

    fun refreshWeather(cityName: String, latitude: Double, longitude: Double) {
        fetchWeather(cityName, latitude, longitude, forceRefresh = true)
    }

    fun refreshWeather() {
        val firstKey = _weatherStates.value.keys.firstOrNull() ?: "Default"
        val state = _weatherStates.value[firstKey]
        if (state?.weather != null) {
            fetchWeather(firstKey, state.weather.latitude, state.weather.longitude, forceRefresh = true)
        }
    }
}
