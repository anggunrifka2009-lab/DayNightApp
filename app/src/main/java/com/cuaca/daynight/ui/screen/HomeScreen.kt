package com.cuaca.daynight.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cuaca.daynight.data.location.LocationClient
import com.cuaca.daynight.data.location.LocationHelper
import com.cuaca.daynight.data.preferences.AppLanguage
import com.cuaca.daynight.data.preferences.SavedCity
import com.cuaca.daynight.data.preferences.SavedCityPreferences
import com.cuaca.daynight.data.preferences.ThemeMode
import com.cuaca.daynight.data.preferences.ThemePreferences
import com.cuaca.daynight.ui.components.*
import com.cuaca.daynight.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    val themePreferences = remember { ThemePreferences(context.applicationContext) }
    val savedCityPreferences = remember { SavedCityPreferences(context.applicationContext) }

    val savedCities = remember {
        mutableStateListOf<SavedCity>().apply {
            val loaded = savedCityPreferences.getSavedCities()
            if (loaded.isNotEmpty()) {
                addAll(loaded)
            } else {
                val defaultCity = SavedCity("Kerten", -7.5614, 110.7852)
                add(defaultCity)
                savedCityPreferences.saveCities(listOf(defaultCity))
            }
        }
    }

    val themeMode by themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val isEnglish = selectedLanguage == AppLanguage.ENGLISH

    var cityName by remember {
        mutableStateOf(savedCities.firstOrNull()?.name ?: if (isEnglish) "Location" else "Lokasi")
    }

    var isManagingCities by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var isGettingLocation by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    fun getWeatherFromLocation() {
        scope.launch {
            try {
                isGettingLocation = true
                locationError = null
                val location = locationClient.getCurrentLocation()

                if (location != null) {
                    val locName = locationHelper.getCityName(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    cityName = locName

                    val exists = savedCities.any { it.name.equals(locName, ignoreCase = true) }
                    if (!exists) {
                        val currentCity = SavedCity(locName, location.latitude, location.longitude)
                        savedCities.add(0, currentCity)
                        savedCityPreferences.saveCities(savedCities.toList())
                    }

                    viewModel.fetchWeather(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                } else {
                    locationError = if (isEnglish) "Location not found." else "Lokasi tidak ditemukan."
                }
            } catch (e: Exception) {
                locationError = e.message ?: "Gagal mendapatkan lokasi."
            } finally {
                isGettingLocation = false
            }
        }
    }

    fun addAndSelectCity(name: String) {
        scope.launch {
            try {
                val coords = locationHelper.getCoordinatesByCity(name)
                if (coords != null) {
                    val newCity = SavedCity(name, coords.first, coords.second)
                    if (savedCities.none { it.name.equals(name, ignoreCase = true) }) {
                        savedCities.add(newCity)
                        savedCityPreferences.saveCities(savedCities.toList())
                    }
                    cityName = name
                    viewModel.fetchWeather(coords.first, coords.second)
                    isManagingCities = false
                }
            } catch (_: Exception) {
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            getWeatherFromLocation()
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

    if (isManagingCities) {
        ManageCitiesScreen(
            savedCities = savedCities,
            currentCityName = cityName,
            isEnglish = isEnglish,
            onBackClick = { isManagingCities = false },
            onCitySelected = { city ->
                cityName = city.name
                viewModel.fetchWeather(city.latitude, city.longitude)
                isManagingCities = false
            },
            onAddCity = { name -> addAndSelectCity(name) },
            onDeleteCity = { city ->
                savedCities.remove(city)
                savedCityPreferences.saveCities(savedCities.toList())
            }
        )
        return
    }

    // SETTINGS DIALOG
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    text = if (isEnglish) "Settings" else "Pengaturan",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (isEnglish) "Appearance" else "Tema Tampilan",
                            style = MaterialTheme.typography.titleSmall
                        )

                        val modes = listOf(
                            ThemeMode.LIGHT to if (isEnglish) "Light" else "Terang",
                            ThemeMode.DARK to if (isEnglish) "Dark" else "Gelap",
                            ThemeMode.SYSTEM to if (isEnglish) "System" else "Sistem"
                        )

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            modes.forEachIndexed { index, (mode, label) ->
                                SegmentedButton(
                                    selected = themeMode == mode,
                                    onClick = {
                                        scope.launch { themePreferences.saveTheme(mode) }
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

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (isEnglish) "Language" else "Bahasa",
                            style = MaterialTheme.typography.titleSmall
                        )

                        LanguageSelector(
                            selectedLanguage = selectedLanguage,
                            onLanguageSelected = onLanguageSelected
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text(if (isEnglish) "Done" else "Selesai")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1B2A3D),
                Color(0xFF131E2C),
                Color(0xFF0C141F)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF5A84C1),
                Color(0xFF4B75AF),
                Color(0xFF3B629B)
            )
        )
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.refreshWeather() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(top = 10.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TOP HEADER BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { isManagingCities = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (isEnglish) "Add City" else "Tambah Kota",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = cityName,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "📍 1 • • • •",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = if (isEnglish) "Menu" else "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isEnglish) "Settings" else "Pengaturan") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                showSettingsDialog = true
                            }
                        )
                    }
                }
            }

            val weather = uiState.weather
            if (weather != null) {
                val current = weather.current
                val dailyMax = weather.daily.temperatureMax.firstOrNull() ?: current.temperature
                val dailyMin = weather.daily.temperatureMin.firstOrNull() ?: current.temperature

                // ── BANNER PERINGATAN CUACA EKSTREM (#7) ──────────────────────
                val todayWeatherCode = weather.daily.weatherCode.firstOrNull() ?: current.weatherCode
                SevereWeatherBanner(
                    weatherCode = todayWeatherCode,
                    isEnglish = isEnglish
                )

                // HERO WEATHER SECTION
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${current.temperature.toInt()}°",
                        color = Color.White,
                        fontSize = 86.sp,
                        fontWeight = FontWeight.ExtraLight
                    )

                    Text(
                        text = "${getWeatherDescription(current.weatherCode, isEnglish)}  ${dailyMax.toInt()}° / ${dailyMin.toInt()}°",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // CARD 1: 5-DAY FORECAST
                val dailyItems = weather.daily.time.indices.take(5).map { idx ->
                    val dateStr = weather.daily.time[idx]
                    val dayName = getDayLabel(dateStr, idx == 0, idx == 1, isEnglish)
                    val desc = getWeatherDescription(weather.daily.weatherCode[idx], isEnglish)
                    val icon = getWeatherIcon(weather.daily.weatherCode[idx], 1)
                    DailyItemData(
                        dayName = dayName,
                        description = desc,
                        weatherIcon = icon,
                        tempMax = weather.daily.temperatureMax[idx],
                        tempMin = weather.daily.temperatureMin[idx]
                    )
                }

                FiveDayForecastCard(
                    dailyItems = dailyItems,
                    isEnglish = isEnglish
                )

                // CARD 2: 24-HOUR FORECAST CHART + CURAH HUJAN (#1)
                val hourlyItems = weather.hourly.time.indices.take(12).map { idx ->
                    val timeStr = weather.hourly.time[idx].substringAfter("T").take(5)
                    val tempVal = weather.hourly.temperature[idx]
                    val icon = getWeatherIcon(weather.hourly.weatherCode[idx], current.isDay)
                    val wind = weather.hourly.windSpeed?.getOrNull(idx) ?: current.windSpeed
                    val rain = weather.hourly.precipitation?.getOrNull(idx) ?: 0.0
                    HourlyItemData(
                        time = timeStr,
                        temp = tempVal,
                        icon = icon,
                        windSpeed = "${String.format(Locale.US, "%.1f", wind)}km/j",
                        precipitationMm = rain
                    )
                }

                HourlyForecastChartCard(
                    hourlyItems = hourlyItems,
                    isEnglish = isEnglish
                )

                // GRID DETAILS SECTION
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WindCompassCard(
                        windSpeedKmH = current.windSpeed,
                        windDirectionDegrees = current.windDirection ?: 225.0,
                        windGustsKmH = current.windGusts,
                        isEnglish = isEnglish,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )

                    WeatherDetailsListCard(
                        humidity = current.humidity,
                        feelsLike = current.feelsLike,
                        uvIndex = current.uvIndex ?: 0.0,
                        pressure = current.pressure ?: 1014.0,
                        rainChance = weather.hourly.precipitationProbability.firstOrNull() ?: 0,
                        dewPoint = current.dewPoint,
                        isEnglish = isEnglish,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }

                // KARTU FAJAR/SENJA + PANJANG HARI (#4)
                val sunriseTime = weather.daily.sunrise.firstOrNull()?.substringAfter("T")?.take(5) ?: "05:08"
                val sunsetTime = weather.daily.sunset.firstOrNull()?.substringAfter("T")?.take(5) ?: "17:38"
                val daylightSec = weather.daily.daylightDuration?.firstOrNull()
                val sunshineSec = weather.daily.sunshineDuration?.firstOrNull()

                SunArcCard(
                    sunriseTime = sunriseTime,
                    sunsetTime = sunsetTime,
                    daylightDurationSeconds = daylightSec,
                    sunshineDurationSeconds = sunshineSec,
                    isEnglish = isEnglish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp)
                )

                // KARTU UV PROTEKSI (#5)
                val uvMax = weather.daily.uvIndexMax?.firstOrNull() ?: current.uvIndex ?: 0.0
                UvProtectionCard(
                    uvIndexMax = uvMax,
                    isEnglish = isEnglish
                )

                // KARTU AQI REAL (#6)
                val aqiData = uiState.airQuality
                if (aqiData != null) {
                    val currentAqi = aqiData.hourly.usAqi.firstOrNull { it != null } ?: 0
                    val currentPm25 = aqiData.hourly.pm25.firstOrNull { it != null }
                    val currentPm10 = aqiData.hourly.pm10.firstOrNull { it != null }
                    AirQualityCard(
                        aqiValue = currentAqi,
                        pm25 = currentPm25,
                        pm10 = currentPm10,
                        isEnglish = isEnglish
                    )
                } else {
                    // Fallback: tampilkan skeleton / placeholder
                    AirQualityCard(
                        aqiValue = 0,
                        isEnglish = isEnglish
                    )
                }

                // FOOTER
                Text(
                    text = if (isEnglish) "Data provided by Open-Meteo" else "Data disediakan oleh Open-Meteo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            } else if (uiState.isLoading || isGettingLocation) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (uiState.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

private fun getDayLabel(
    dateString: String,
    isToday: Boolean,
    isTomorrow: Boolean,
    isEnglish: Boolean
): String {
    if (isToday) return if (isEnglish) "Today" else "Hari ini"
    if (isTomorrow) return if (isEnglish) "Tomorrow" else "Besok"

    return try {
        val date = LocalDate.parse(dateString)
        val locale = if (isEnglish) Locale.ENGLISH else Locale("id", "ID")
        date.format(DateTimeFormatter.ofPattern("EEE", locale))
    } catch (_: Exception) {
        dateString
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
        3 -> if (isEnglish) "Overcast" else "Berawan"
        45, 48 -> if (isEnglish) "Fog" else "Berkabut"
        51, 53, 55 -> if (isEnglish) "Drizzle" else "Gerimis"
        61, 63, 65 -> if (isEnglish) "Rain" else "Hujan"
        66, 67 -> if (isEnglish) "Freezing rain" else "Hujan beku"
        71, 73, 75, 77 -> if (isEnglish) "Snow" else "Salju"
        80, 81, 82 -> if (isEnglish) "Rain showers" else "Hujan deras"
        85, 86 -> if (isEnglish) "Snow showers" else "Hujan salju"
        95, 96, 99 -> if (isEnglish) "Thunderstorm" else "Badai petir"
        else -> if (isEnglish) "Unknown" else "Berawan"
    }
}

private fun getWeatherIcon(
    code: Int,
    isDay: Int
): String {
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
        else -> "🌤️"
    }
}
