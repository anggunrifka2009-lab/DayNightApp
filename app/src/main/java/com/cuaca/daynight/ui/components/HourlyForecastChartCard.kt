package com.cuaca.daynight.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HourlyItemData(
    val time: String,
    val temp: Double,
    val icon: String,
    val windSpeed: String
)

@Composable
fun HourlyForecastChartCard(
    hourlyItems: List<HourlyItemData>,
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "🕒",
                    fontSize = 16.sp
                )
                Text(
                    text = if (isEnglish) "24-hour forecast" else "Ramalan 24 jam",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            if (hourlyItems.isNotEmpty()) {
                val scrollState = rememberScrollState()
                val itemWidthPx = 70f
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
                                    color = Color.White,
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
                            // Invert Y axis: higher temp -> lower Y
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

                            // Draw smooth yellow curve
                            drawPath(
                                path = path,
                                color = Color(0xFFFFC107),
                                style = Stroke(width = 4f)
                            )

                            // Highlight current point (first point)
                            drawCircle(
                                color = Color.White,
                                radius = 6f,
                                center = points.first()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Icons, Wind speeds, and Times below curve
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        hourlyItems.forEachIndexed { index, item ->
                            Column(
                                modifier = Modifier.width(64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = item.icon,
                                    fontSize = 22.sp
                                )

                                Text(
                                    text = item.windSpeed,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 11.sp
                                )

                                Text(
                                    text = if (index == 0) {
                                        if (isEnglish) "Now" else "Sekarang"
                                    } else {
                                        item.time
                                    },
                                    color = Color.White.copy(alpha = 0.9f),
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
