package com.cuaca.daynight.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cuaca.daynight.ui.components.DailyForecastCard
import com.cuaca.daynight.ui.components.HourlyForecastCard
import com.cuaca.daynight.ui.components.WeatherCard
import com.cuaca.daynight.ui.components.WeatherInfoCard
import com.cuaca.daynight.viewmodel.WeatherViewModel

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val uiState by weatherViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        weatherViewModel.fetchWeather()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "DayNight",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Informasi cuaca hari ini",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        when {
            uiState.isLoading -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Memuat data cuaca...")
                    }
                }
            }

            uiState.error != null -> {
                item {
                    Text(
                        text = "Gagal mengambil data: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            uiState.weather != null -> {
                val weather = uiState.weather!!
                val current = weather.current

                item {
                    WeatherCard(
                        city = "Yogyakarta",
                        temperature = current.temperature,
                        description = getWeatherDescription(
                            current.weatherCode
                        ),
                        weatherIcon = getWeatherIcon(
                            current.weatherCode,
                            current.isDay
                        )
                    )
                }

                item {
                    Column {
                        Text(
                            text = "Prakiraan Per Jam",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                weather.hourly.time.indices
                                    .take(8)
                                    .toList()
                            ) { index ->
                                HourlyForecastCard(
                                    time = weather.hourly.time[index]
                                        .substringAfter("T")
                                        .take(5),
                                    temperature = weather.hourly.temperature[index],
                                    weatherIcon = getWeatherIcon(
                                        weather.hourly.weatherCode[index],
                                        current.isDay
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text(
                            text = "Informasi Cuaca",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            WeatherInfoCard(
                                title = "Kelembapan",
                                value = "${current.humidity}%",
                                icon = "💧",
                                modifier = Modifier.weight(1f)
                            )

                            WeatherInfoCard(
                                title = "Kecepatan Angin",
                                value = "${current.windSpeed} km/j",
                                icon = "💨",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    val rainChance =
                        weather.hourly.precipitationProbability.firstOrNull() ?: 0

                    WeatherInfoCard(
                        title = "Kemungkinan Hujan",
                        value = "$rainChance%",
                        icon = "🌧️",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = "Prakiraan 7 Hari",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                items(weather.daily.time.indices.toList()) { index ->
                    DailyForecastCard(
                        day = weather.daily.time[index],
                        weatherIcon = getWeatherIcon(
                            weather.daily.weatherCode[index],
                            1
                        ),
                        description = getWeatherDescription(
                            weather.daily.weatherCode[index]
                        ),
                        temperatureMin = weather.daily.temperatureMin[index],
                        temperatureMax = weather.daily.temperatureMax[index]
                    )
                }
            }
        }
    }
}

private fun getWeatherDescription(code: Int): String {
    return when (code) {
        0 -> "Cerah"
        1 -> "Cerah berawan"
        2 -> "Berawan sebagian"
        3 -> "Mendung"
        45, 48 -> "Berkabut"
        51, 53, 55 -> "Gerimis"
        61, 63, 65 -> "Hujan"
        66, 67 -> "Hujan beku"
        71, 73, 75, 77 -> "Salju"
        80, 81, 82 -> "Hujan deras"
        85, 86 -> "Hujan salju"
        95, 96, 99 -> "Badai petir"
        else -> "Tidak diketahui"
    }
}

private fun getWeatherIcon(code: Int, isDay: Int): String {
    return when (code) {
        0 -> if (isDay == 1) "☀️" else "🌙"
        1, 2 -> if (isDay == 1) "🌤️" else "☁️"
        3 -> "☁️"
        45, 48 -> "🌫️"
        51, 53, 55, 61, 63, 65 -> "🌧️"
        66, 67 -> "🌧️"
        71, 73, 75, 77 -> "❄️"
        80, 81, 82 -> "🌦️"
        85, 86 -> "🌨️"
        95, 96, 99 -> "⛈️"
        else -> "🌡️"
    }
}

// PREVIEW HOME SCREEN

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "DayNight Home Preview"
)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "DayNight",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Informasi cuaca hari ini",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                WeatherCard(
                    city = "Yogyakarta",
                    temperature = 29.0,
                    description = "Cerah berawan",
                    weatherIcon = "🌤️"
                )
            }

            item {
                Text(
                    text = "Prakiraan Per Jam",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(6) { index ->
                        HourlyForecastCard(
                            time = "${10 + index}:00",
                            temperature = 29.0 - index,
                            weatherIcon = "🌤️"
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Informasi Cuaca",
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WeatherInfoCard(
                        title = "Kelembapan",
                        value = "75%",
                        icon = "💧",
                        modifier = Modifier.weight(1f)
                    )

                    WeatherInfoCard(
                        title = "Kecepatan Angin",
                        value = "12 km/j",
                        icon = "💨",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                WeatherInfoCard(
                    title = "Kemungkinan Hujan",
                    value = "40%",
                    icon = "🌧️",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(
                    text = "Prakiraan 7 Hari",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(7) { index ->
                DailyForecastCard(
                    day = "Hari ke-${index + 1}",
                    weatherIcon = "🌤️",
                    description = "Cerah berawan",
                    temperatureMin = 23.0,
                    temperatureMax = 30.0
                )
            }
        }
    }
}
