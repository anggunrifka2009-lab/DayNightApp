package com.cuaca.daynight.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun WindCompassCard(
    windSpeedKmH: Double,
    windDirectionDegrees: Double,
    windGustsKmH: Double? = null,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (isDark) Color(0x66294D7A) else Color(0xDDFFFFFF)
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val subtextColor = if (isDark) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)

    val directionText = getWindDirectionText(windDirectionDegrees, isEnglish)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = directionText,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${windSpeedKmH.toInt()} km/j",
                    color = subtextColor,
                    fontSize = 13.sp
                )
                // Wind Gusts line
                if (windGustsKmH != null) {
                    Text(
                        text = "💨 ${if (isEnglish) "Gusts" else "Hembusan"} ${windGustsKmH.toInt()} km/j",
                        color = subtextColor,
                        fontSize = 12.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp),
                contentAlignment = Alignment.Center
            ) {
                val labelNorth = if (isEnglish) "N" else "U"
                val labelEast = if (isEnglish) "E" else "T"
                val labelSouth = "S"
                val labelWest = if (isEnglish) "W" else "B"

                Text(
                    text = labelNorth,
                    color = subtextColor,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Text(
                    text = labelSouth,
                    color = subtextColor,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                Text(
                    text = labelWest,
                    color = subtextColor,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = labelEast,
                    color = subtextColor,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )

                Canvas(
                    modifier = Modifier.size(70.dp)
                ) {
                    val radius = size.minDimension / 2f
                    val center = Offset(radius, radius)

                    drawCircle(
                        color = subtextColor.copy(alpha = 0.4f),
                        radius = radius - 2f,
                        style = Stroke(width = 2f)
                    )

                    rotate(degrees = windDirectionDegrees.toFloat(), pivot = center) {
                        val path = Path().apply {
                            moveTo(center.x, center.y - radius + 10f)
                            lineTo(center.x - 6f, center.y + 12f)
                            lineTo(center.x, center.y + 4f)
                            lineTo(center.x + 6f, center.y + 12f)
                            close()
                        }
                        drawPath(path = path, color = textColor)
                    }
                }
            }
        }
    }
}

@Composable
fun SunArcCard(
    sunriseTime: String,
    sunsetTime: String,
    daylightDurationSeconds: Double? = null,
    sunshineDurationSeconds: Double? = null,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (isDark) Color(0x66294D7A) else Color(0xDDFFFFFF)
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val subtextColor = if (isDark) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)

    val fajarLabel = if (isEnglish) "Dawn" else "Fajar"
    val senjaLabel = if (isEnglish) "Dusk" else "Senja"

    fun formatDuration(seconds: Double): String {
        val totalMinutes = (seconds / 60).roundToInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (isEnglish) "${hours}h ${minutes}m" else "${hours}j ${minutes}m"
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🌅 $sunriseTime $fajarLabel",
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "🌇 $sunsetTime $senjaLabel",
                        color = subtextColor,
                        fontSize = 12.sp
                    )
                }
                if (daylightDurationSeconds != null) {
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (isEnglish) "Daylight" else "Panjang Hari",
                            color = subtextColor,
                            fontSize = 11.sp
                        )
                        Text(
                            text = formatDuration(daylightDurationSeconds),
                            color = textColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (sunshineDurationSeconds != null) {
                            Text(
                                text = "☀️ ${formatDuration(sunshineDurationSeconds)}",
                                color = subtextColor,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .padding(horizontal = 8.dp)
            ) {
                val width = size.width
                val height = size.height

                val path = Path().apply {
                    moveTo(0f, height * 0.9f)
                    quadraticTo(
                        width / 2f, -height * 0.3f,
                        width, height * 0.9f
                    )
                }

                drawPath(
                    path = path,
                    color = subtextColor.copy(alpha = 0.5f),
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )

                val sunX = width * 0.35f
                val sunY = height * 0.35f

                drawCircle(
                    color = textColor.copy(alpha = 0.3f),
                    radius = 12f,
                    center = Offset(sunX, sunY)
                )
                drawCircle(
                    color = textColor,
                    radius = 6f,
                    center = Offset(sunX, sunY)
                )
            }
        }
    }
}

@Composable
fun WeatherDetailsListCard(
    humidity: Int,
    feelsLike: Double,
    uvIndex: Double,
    pressure: Double,
    rainChance: Int,
    dewPoint: Double? = null,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (isDark) Color(0x66294D7A) else Color(0xDDFFFFFF)
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val subtextColor = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailRow(
                label = if (isEnglish) "Humidity" else "Kelembaban",
                value = "$humidity%",
                labelColor = subtextColor,
                valueColor = textColor
            )
            DetailRow(
                label = if (isEnglish) "Feels like" else "Terasa seperti",
                value = "${feelsLike.toInt()}°",
                labelColor = subtextColor,
                valueColor = textColor
            )
            if (dewPoint != null) {
                DetailRow(
                    label = if (isEnglish) "Dew Point" else "Titik Embun",
                    value = "${dewPoint.toInt()}°",
                    labelColor = subtextColor,
                    valueColor = textColor
                )
            }
            DetailRow(
                label = if (isEnglish) "Pressure" else "Tekanan",
                value = "${pressure.toInt()}mbar",
                labelColor = subtextColor,
                valueColor = textColor
            )
            DetailRow(
                label = if (isEnglish) "Rain chance" else "Peluang hujan",
                value = "$rainChance%",
                labelColor = subtextColor,
                valueColor = textColor
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = labelColor,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AirQualityCard(
    aqiValue: Int,
    pm25: Double? = null,
    pm10: Double? = null,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (isDark) Color(0x66294D7A) else Color(0xDDFFFFFF)
    val subtextColor = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B)

    val (aqiLabel, aqiColor) = getAqiInfo(aqiValue, isEnglish)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🍃", fontSize = 18.sp)
                    Column {
                        Text(
                            text = "${if (isEnglish) "AQI" else "IKU"} $aqiValue – $aqiLabel",
                            color = aqiColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (pm25 != null) {
                            Text(
                                text = "PM2.5: ${String.format("%.1f", pm25)} µg/m³",
                                color = subtextColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                if (pm10 != null) {
                    Text(
                        text = "PM10: ${pm10.roundToInt()} µg/m³",
                        color = subtextColor,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun UvProtectionCard(
    uvIndexMax: Double,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (isDark) Color(0x66294D7A) else Color(0xDDFFFFFF)
    val subtextColor = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B)

    val uvInt = uvIndexMax.roundToInt()
    val (uvLabel, uvColor, uvEmoji, uvAdvice) = getUvInfo(uvInt, isEnglish)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(uvEmoji, fontSize = 20.sp)
                Column {
                    Text(
                        text = "UV $uvInt – $uvLabel",
                        color = uvColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uvAdvice,
                        color = subtextColor,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SevereWeatherBanner(
    weatherCode: Int,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isSevere = weatherCode in 80..82 || weatherCode in 95..99
    if (!isSevere) return

    val isThunderstorm = weatherCode in 95..99
    val bannerColor = if (isThunderstorm) Color(0xFFB71C1C) else Color(0xFFE65100)
    val emoji = if (isThunderstorm) "⛈️" else "🌧️"
    val title = if (isThunderstorm) {
        if (isEnglish) "Thunderstorm Warning!" else "⚠️ Peringatan Badai Petir!"
    } else {
        if (isEnglish) "Heavy Rain Warning!" else "⚠️ Peringatan Hujan Lebat!"
    }
    val desc = if (isThunderstorm) {
        if (isEnglish) "Thunderstorm forecast today. Avoid outdoor activities." else "Diprakirakan ada badai petir hari ini. Hindari aktivitas di luar ruangan."
    } else {
        if (isEnglish) "Heavy rain forecast today. Be careful when traveling." else "Diprakirakan hujan lebat hari ini. Hati-hati saat bepergian."
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bannerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(emoji, fontSize = 28.sp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = desc,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun getWindDirectionText(degrees: Double, isEnglish: Boolean): String {
    val normDeg = (degrees % 360 + 360) % 360
    return when {
        normDeg in 22.5..67.5 -> if (isEnglish) "North East" else "Timur Laut"
        normDeg in 67.5..112.5 -> if (isEnglish) "East" else "Timur"
        normDeg in 112.5..157.5 -> if (isEnglish) "South East" else "Tenggara"
        normDeg in 157.5..202.5 -> if (isEnglish) "South" else "Selatan"
        normDeg in 202.5..247.5 -> if (isEnglish) "South West" else "Barat Daya"
        normDeg in 247.5..292.5 -> if (isEnglish) "West" else "Barat"
        normDeg in 292.5..337.5 -> if (isEnglish) "North West" else "Barat Laut"
        else -> if (isEnglish) "North" else "Utara"
    }
}

private fun getAqiInfo(aqi: Int, isEnglish: Boolean): Pair<String, Color> {
    return when {
        aqi <= 50 -> Pair(if (isEnglish) "Good" else "Baik", Color(0xFF4CAF50))
        aqi <= 100 -> Pair(if (isEnglish) "Moderate" else "Sedang", Color(0xFFFFC107))
        aqi <= 150 -> Pair(if (isEnglish) "Unhealthy for Sensitive" else "Tidak Sehat (Sensitif)", Color(0xFFFF9800))
        aqi <= 200 -> Pair(if (isEnglish) "Unhealthy" else "Tidak Sehat", Color(0xFFF44336))
        aqi <= 300 -> Pair(if (isEnglish) "Very Unhealthy" else "Sangat Tidak Sehat", Color(0xFF9C27B0))
        else -> Pair(if (isEnglish) "Hazardous" else "Berbahaya", Color(0xFF7B1FA2))
    }
}

private data class UvInfo(val label: String, val color: Color, val emoji: String, val advice: String)

private fun getUvInfo(uv: Int, isEnglish: Boolean): UvInfo {
    return when {
        uv <= 2 -> UvInfo(
            label = if (isEnglish) "Low" else "Rendah",
            color = Color(0xFF4CAF50),
            emoji = "🌤️",
            advice = if (isEnglish) "No protection needed." else "Tidak perlu perlindungan khusus."
        )
        uv <= 5 -> UvInfo(
            label = if (isEnglish) "Moderate" else "Sedang",
            color = Color(0xFFFFC107),
            emoji = "☀️",
            advice = if (isEnglish) "Wear sunscreen if outdoors." else "Gunakan sunscreen jika beraktivitas di luar."
        )
        uv <= 7 -> UvInfo(
            label = if (isEnglish) "High" else "Tinggi",
            color = Color(0xFFFF9800),
            emoji = "🔆",
            advice = if (isEnglish) "Sunscreen + hat recommended." else "Pakai sunscreen & topi, kurangi paparan siang."
        )
        uv <= 10 -> UvInfo(
            label = if (isEnglish) "Very High" else "Sangat Tinggi",
            color = Color(0xFFF44336),
            emoji = "🌡️",
            advice = if (isEnglish) "Extra protection needed." else "Perlindungan ekstra diperlukan, hindari paparan."
        )
        else -> UvInfo(
            label = if (isEnglish) "Extreme" else "Ekstrem",
            color = Color(0xFF9C27B0),
            emoji = "☢️",
            advice = if (isEnglish) "Avoid outdoor activities!" else "Hindari aktivitas di luar ruangan!"
        )
    }
}
