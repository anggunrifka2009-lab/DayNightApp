package com.cuaca.daynight.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

data class HourlyItemData(
    val time: String,
    val temp: Double,
    val icon: String,
    val windSpeed: String,
    val precipitationMm: Double = 0.0
)

@Composable
fun HourlyForecastChartCard(
    hourlyItems: List<HourlyItemData>,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (isDark) Color(0x66294D7A) else Color(0xDDFFFFFF)
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val subtextColor = if (isDark) Color.White.copy(alpha = 0.75f) else Color(0xFF64748B)
    val curveColor = if (isDark) Color(0xFFFFC107) else Color(0xFFF59E0B)
    val rainColor = Color(0xFF42A5F5)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isEnglish) "24-hour forecast" else "Ramalan 24 jam",
                    color = textColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            if (hourlyItems.isNotEmpty()) {
                val scrollState = rememberScrollState()
                val chartHeightDp = 50.dp

                val minTemp = hourlyItems.minOf { it.temp }
                val maxTemp = hourlyItems.maxOf { it.temp }
                val tempRange = (maxTemp - minTemp).coerceAtLeast(1.0)

                Column(
                    modifier = Modifier.horizontalScroll(scrollState)
                ) {
                    // Temperature labels above chart
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        hourlyItems.forEach { item ->
                            Box(
                                modifier = Modifier.width(64.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${item.temp.toInt()}°",
                                    color = textColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Curve Chart Canvas
                    Canvas(
                        modifier = Modifier
                            .width((hourlyItems.size * 64).dp)
                            .height(chartHeightDp)
                            .padding(horizontal = 20.dp)
                    ) {
                        val widthPx = size.width
                        val heightPx = size.height
                        val stepX = if (hourlyItems.size > 1) widthPx / (hourlyItems.size - 1) else widthPx

                        val points = hourlyItems.mapIndexed { index, item ->
                            val x = index * stepX
                            val normalizedTemp = (item.temp - minTemp) / tempRange
                            val y = (heightPx * 0.85f) - (normalizedTemp * heightPx * 0.7f)
                            Offset(x, y.toFloat())
                        }

                        if (points.size > 1) {
                            val path = Path()
                            path.moveTo(points.first().x, points.first().y)

                            for (i in 0 until points.size - 1) {
                                val p1 = points[i]
                                val p2 = points[i + 1]
                                val controlX1 = p1.x + (p2.x - p1.x) / 2f
                                val controlY1 = p1.y
                                val controlX2 = p1.x + (p2.x - p1.x) / 2f
                                val controlY2 = p2.y
                                path.cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
                            }

                            drawPath(
                                path = path,
                                color = curveColor,
                                style = Stroke(width = 4f)
                            )

                            drawCircle(
                                color = textColor,
                                radius = 6f,
                                center = points.first()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Icons, Rain mm, Wind speeds, Times
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        hourlyItems.forEachIndexed { index, item ->
                            Column(
                                modifier = Modifier.width(64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = item.icon,
                                    fontSize = 22.sp
                                )

                                // Curah hujan (mm) – tampilkan jika > 0
                                if (item.precipitationMm > 0.0) {
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", item.precipitationMm)} mm",
                                        color = rainColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                } else {
                                    Text(
                                        text = "–",
                                        color = subtextColor,
                                        fontSize = 10.sp
                                    )
                                }

                                Text(
                                    text = item.windSpeed,
                                    color = subtextColor,
                                    fontSize = 11.sp
                                )

                                Text(
                                    text = if (index == 0) {
                                        if (isEnglish) "Now" else "Sekarang"
                                    } else {
                                        item.time
                                    },
                                    color = textColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
