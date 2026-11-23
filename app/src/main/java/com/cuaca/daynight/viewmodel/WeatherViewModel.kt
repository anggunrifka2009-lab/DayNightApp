package com.cuaca.daynight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuaca.daynight.data.model.WeatherResponse
import com.cuaca.daynight.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val weather: WeatherResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun fetchWeather(
        latitude: Double = -7.7956,
        longitude: Double = 110.3695
    ) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState(isLoading = true)

            try {
                val result = repository.getWeather(
                    latitude = latitude,
                    longitude = longitude
                )

                _uiState.value = WeatherUiState(
                    weather = result,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = WeatherUiState(
                    isLoading = false,
                    error = e.message ?: "Gagal mengambil data cuaca"
                )
            }
        }
    }
}
