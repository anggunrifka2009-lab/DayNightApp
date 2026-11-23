package com.cuaca.daynight.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cuaca.daynight.data.location.LocationClient
import com.cuaca.daynight.data.location.LocationHelper
import com.cuaca.daynight.data.preferences.AppLanguage
import com.cuaca.daynight.data.preferences.ThemeMode
import com.cuaca.daynight.data.preferences.ThemePreferences
import com.cuaca.daynight.ui.components.DailyForecastCard
import com.cuaca.daynight.ui.components.HourlyForecastCard
import com.cuaca.daynight.ui.components.LanguageSelector
import com.cuaca.daynight.ui.components.WeatherCard
import com.cuaca.daynight.ui.components.WeatherInfoCard
import com.cuaca.daynight.ui.theme.DayNightTheme
import com.cuaca.daynight.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: WeatherViewModel = viewModel(),
    selectedLanguage: AppLanguage = AppLanguage.INDONESIAN,
    onLanguageSelected: (AppLanguage) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val locationClient = remember { LocationClient(context) }
    val locationHelper = remember { LocationHelper(context) }

    val themePreferences = remember {
        ThemePreferences(context.applicationContext)
    }

    val themeMode by themePreferences.themeMode.collectAsState(
        initial = ThemeMode.SYSTEM
    )

    val isEnglish = selectedLanguage == AppLanguage.ENGLISH

    var cityName by remember {
        mutableStateOf("Mencari lokasi...")
    }

    var cityInput by remember {
        mutableStateOf("")
    }

    var locationError by remember {
        mutableStateOf<String?>(null)
    }

    var searchError by remember {
        mutableStateOf<String?>(null)
    }

    var isGettingLocation by remember {
        mutableStateOf(false)
    }

    var isSearchingCity by remember {
        mutableStateOf(false)
    }

    fun getWeatherFromLocation() {
        scope.launch {
            try {
                isGettingLocation = true
                locationError = null

                val location = locationClient.getCurrentLocation()

                if (location != null) {
                    cityName = locationHelper.getCityName(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )

                    viewModel.fetchWeather(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                } else {
                    locationError = if (isEnglish) {
                        "Location not found. Please enable GPS."
                    } else {
                        "Lokasi tidak ditemukan. Coba aktifkan GPS HP."
                    }
                }
            } catch (e: Exception) {
                locationError = if (isEnglish) {
                    "Failed to get device location."
                } else {
                    e.message ?: "Gagal mendapatkan lokasi."
                }
            } finally {
                isGettingLocation = false
            }
        }
    }

    fun searchCity() {
        if (cityInput.isBlank()) {
            searchError = if (isEnglish) {
                "Please enter a city name."
            } else {
                "Masukkan nama kota terlebih dahulu."
            }
            return
        }

        scope.launch {
            try {
                isSearchingCity = true
                searchError = null

                val coordinates =
                    locationHelper.getCoordinatesByCity(cityInput.trim())

                if (coordinates != null) {
                    cityName = cityInput.trim()

                    viewModel.fetchWeather(
                        latitude = coordinates.first,
                        longitude = coordinates.second
                    )
                } else {
                    searchError = if (isEnglish) {
                        "City not found."
                    } else {
                        "Kota tidak ditemukan."
                    }
                }
            } catch (e: Exception) {
                searchError = if (isEnglish) {
                    "Failed to search for the city."
                } else {
                    "Gagal mencari lokasi kota."
                }
            } finally {
                isSearchingCity = false
            }
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.values.any { it }

            if (granted) {
                getWeatherFromLocation()
            } else {
                locationError = if (isEnglish) {
                    "Location permission is required to show local weather."
                } else {
                    "Izin lokasi diperlukan untuk menampilkan cuaca di sekitar kamu."
                }
            }
        }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
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
                    text = if (isEnglish) {
                        "Today's weather information"
                    } else {
                        "Informasi cuaca hari ini"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            LanguageSelector(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
        }

        item {
            Column {
                Text(
                    text = if (isEnglish) "Appearance Theme" else "Tema Tampilan",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                val modes = listOf(
                    ThemeMode.LIGHT to if (isEnglish) "Light" else "Terang",
                    ThemeMode.DARK to if (isEnglish) "Dark" else "Gelap",
                    ThemeMode.SYSTEM to if (isEnglish) "System" else "Sistem"
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    modes.forEachIndexed { index, (mode, label) ->
                        SegmentedButton(
                            selected = themeMode == mode,
                            onClick = {
                                scope.launch {
                                    themePreferences.saveTheme(mode)
                                }
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = modes.size
                            )
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }

        item {
            Column {
                OutlinedTextField(
                    value = cityInput,
                    onValueChange = {
                        cityInput = it
                        searchError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(if (isEnglish) "Search city" else "Cari nama kota")
                    },
                    placeholder = {
                        Text(if (isEnglish) "Example: Jakarta" else "Contoh: Jakarta")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { searchCity() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSearchingCity
                ) {
                    if (isSearchingCity) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (isEnglish) "Search Weather" else "Cari Cuaca")
                    }
                }

                if (searchError != null) {
                    Text(
                        text = searchError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        when {
            uiState.isLoading || isGettingLocation || isSearchingCity -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = when {
                                isGettingLocation -> if (isEnglish) {
                                    "Getting device location..."
                                } else {
                                    "Mencari lokasi HP..."
                                }

                                isSearchingCity -> if (isEnglish) {
                                    "Searching city..."
                                } else {
                                    "Mencari kota..."
                                }

                                else -> if (isEnglish) {
                                    "Loading weather data..."
                                } else {
                                    "Memuat data cuaca..."
                                }
                            }
                        )
                    }
                }
            }

            uiState.error != null && uiState.weather == null -> {
                item {
                    Text(
                        text = if (isEnglish) {
                            "Failed to get weather data: ${uiState.error}"
                        } else {
                            "Gagal mengambil data: ${uiState.error}"
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            locationError != null && uiState.weather == null -> {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = locationError!!,
                            color = MaterialTheme.colorScheme.error
                        )

                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text(
                                if (isEnglish) "Grant Location Permission"
                                else "Izinkan Lokasi"
                            )
                        }
                    }
                }
            }

            uiState.weather != null -> {
                val weather = uiState.weather!!
                val current = weather.current

                item {
                    WeatherCard(
                        city = cityName,
                        temperature = current.temperature,
                        description = getWeatherDescription(
                            current.weatherCode,
                            isEnglish
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
                            text = if (isEnglish) "Hourly Forecast" else "Prakiraan Per Jam",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                weather.hourly.time.indices.take(8).toList()
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
                            text = if (isEnglish) "Weather Information" else "Informasi Cuaca",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            WeatherInfoCard(
                                title = if (isEnglish) "Humidity" else "Kelembapan",
                                value = "${current.humidity}%",
                                icon = "💧",
                                modifier = Modifier.weight(1f)
                            )

                            WeatherInfoCard(
                                title = if (isEnglish) "Wind Speed" else "Kecepatan Angin",
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
                        title = if (isEnglish) "Rain Probability" else "Kemungkinan Hujan",
                        value = "$rainChance%",
                        icon = "🌧️",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = if (isEnglish) "7-Day Forecast" else "Prakiraan 7 Hari",
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
                            weather.daily.weatherCode[index],
                            isEnglish
                        ),
                        temperatureMin = weather.daily.temperatureMin[index],
                        temperatureMax = weather.daily.temperatureMax[index]
                    )
                }
            }
        }
    }
}

private fun getWeatherDescription(
    code: Int,
    isEnglish: Boolean = false
): String {
    return when (code) {
        0 -> if (isEnglish) "Clear sky" else "Cerah"
        1 -> if (isEnglish) "Mainly clear" else "Cerah berawan"
        2 -> if (isEnglish) "Partly cloudy" else "Berawan sebagian"
        3 -> if (isEnglish) "Overcast" else "Mendung"
        45, 48 -> if (isEnglish) "Fog" else "Berkabut"
        51, 53, 55 -> if (isEnglish) "Drizzle" else "Gerimis"
        61, 63, 65 -> if (isEnglish) "Rain" else "Hujan"
        66, 67 -> if (isEnglish) "Freezing rain" else "Hujan beku"
        71, 73, 75, 77 -> if (isEnglish) "Snow" else "Salju"
        80, 81, 82 -> if (isEnglish) "Rain showers" else "Hujan deras"
        85, 86 -> if (isEnglish) "Snow showers" else "Hujan salju"
        95, 96, 99 -> if (isEnglish) "Thunderstorm" else "Badai petir"
        else -> if (isEnglish) "Unknown" else "Tidak diketahui"
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

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "DayNight Home - Indonesia"
)
@Composable
fun HomeScreenPreview() {
    DayNightTheme {
        HomeScreenPreviewContent(
            language = AppLanguage.INDONESIAN
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "DayNight Home - English"
)
@Composable
fun HomeScreenEnglishPreview() {
    DayNightTheme {
        HomeScreenPreviewContent(
            language = AppLanguage.ENGLISH
        )
    }
}

@Composable
private fun HomeScreenPreviewContent(language: AppLanguage) {
    val isEnglish = language == AppLanguage.ENGLISH

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "DayNight",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = if (isEnglish) {
                    "Today's weather information"
                } else {
                    "Informasi cuaca hari ini"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            LanguageSelector(
                selectedLanguage = language,
                onLanguageSelected = {}
            )
        }

        item {
            WeatherCard(
                city = "Yogyakarta",
                temperature = 29.0,
                description = if (isEnglish) "Partly cloudy" else "Cerah berawan",
                weatherIcon = "🌤️"
            )
        }

        item {
            Text(
                text = if (isEnglish) "Hourly Forecast" else "Prakiraan Per Jam",
                style = MaterialTheme.typography.titleLarge
            )

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
                text = if (isEnglish) "Weather Information" else "Informasi Cuaca",
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WeatherInfoCard(
                    title = if (isEnglish) "Humidity" else "Kelembapan",
                    value = "75%",
                    icon = "💧",
                    modifier = Modifier.weight(1f)
                )

                WeatherInfoCard(
                    title = if (isEnglish) "Wind Speed" else "Kecepatan Angin",
                    value = "12 km/j",
                    icon = "💨",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            WeatherInfoCard(
                title = if (isEnglish) "Rain Probability" else "Kemungkinan Hujan",
                value = "40%",
                icon = "🌧️",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = if (isEnglish) "7-Day Forecast" else "Prakiraan 7 Hari",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(7) { index ->
            DailyForecastCard(
                day = "Day ${index + 1}",
                weatherIcon = "🌤️",
                description = if (isEnglish) "Partly cloudy" else "Cerah berawan",
                temperatureMin = 23.0,
                temperatureMax = 30.0
            )
        }
    }
}
