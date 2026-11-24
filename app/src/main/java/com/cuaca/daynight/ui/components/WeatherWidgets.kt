package com.cuaca.daynight.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WindCompassCard(
    windSpeedKmH: Double,
    windDirectionDegrees: Double,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    val directionText = getWindDirectionText(windDirectionDegrees, isEnglish)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x66294D7A)
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
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${windSpeedKmH.toInt()}km/j",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp),
                contentAlignment = Alignment.Center
            ) {
                // Dial markers (U, T, S, B or N, E, S, W)
                val labelNorth = if (isEnglish) "N" else "U"
                val labelEast = if (isEnglish) "E" else "T"
                val labelSouth = "S"
                val labelWest = if (isEnglish) "W" else "B"

                Text(
                    text = labelNorth,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Text(
                    text = labelSouth,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                Text(
                    text = labelWest,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = labelEast,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )

                Canvas(
                    modifier = Modifier.size(70.dp)
                ) {
                    val radius = size.minDimension / 2f
                    val center = Offset(radius, radius)

                    // Dial Outer Ring
                    drawCircle(
                        color = Color.White.copy(alpha = 0.25f),
                        radius = radius - 2f,
                        style = Stroke(width = 2f)
                    )

                    // Compass Pointer Arrow rotated to wind direction
                    rotate(degrees = windDirectionDegrees.toFloat(), pivot = center) {
                        val path = Path().apply {
                            moveTo(center.x, center.y - radius + 10f)
                            lineTo(center.x - 6f, center.y + 12f)
                            lineTo(center.x, center.y + 4f)
                            lineTo(center.x + 6f, center.y + 12f)
                            close()
                        }
                        drawPath(path = path, color = Color.White)
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
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    val fajarLabel = if (isEnglish) "Dawn" else "Fajar"
    val senjaLabel = if (isEnglish) "Dusk" else "Senja"

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x66294D7A)
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
                    text = "$sunriseTime $fajarLabel",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$sunsetTime $senjaLabel",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(horizontal = 8.dp)
            ) {
                val width = size.width
                val height = size.height

                // Draw dashed/solid sun trajectory arc
                val path = Path().apply {
                    moveTo(0f, height * 0.9f)
                    quadraticTo(
                        width / 2f, -height * 0.3f,
                        width, height * 0.9f
                    )
                }

                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.4f),
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )

                // Sun point along arc (at ~35% progression as example)
                val sunX = width * 0.35f
                val sunY = height * 0.35f

                // Glow ring
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = 12f,
                    center = Offset(sunX, sunY)
                )
                // Sun core
                drawCircle(
                    color = Color.White,
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
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x66294D7A)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailRow(
                label = if (isEnglish) "Humidity" else "Kelembaban",
                value = "$humidity%"
            )
            DetailRow(
                label = if (isEnglish) "Feels like" else "Terasa seperti",
                value = "${feelsLike.toInt()}°"
            )
            DetailRow(
                label = "UV",
                value = "${uvIndex.toInt()}"
            )
            DetailRow(
                label = if (isEnglish) "Pressure" else "Tekanan",
                value = "${pressure.toInt()}mbar"
            )
            DetailRow(
                label = if (isEnglish) "Rain chance" else "Peluang hujan",
                value = "$rainChance%"
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AirQualityCard(
    aqiValue: Int = 62,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x66294D7A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("🍃", fontSize = 16.sp)
                Text(
                    text = "${if (isEnglish) "AQI" else "IKU"} $aqiValue",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = if (isEnglish) "Full air quality forecast ▸" else "Ramalan kualitas udara sepenuhnya ▸",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
        }
    }
}

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
