package com.cuaca.daynight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DailyItemData(
    val dayName: String,
    val description: String,
    val weatherIcon: String,
    val tempMax: Double,
    val tempMin: Double
)

@Composable
fun FiveDayForecastCard(
    dailyItems: List<DailyItemData>,
    isEnglish: Boolean = false,
    onMoreDetailsClick: () -> Unit = {},
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🗓️", fontSize = 16.sp)
                    Text(
                        text = if (isEnglish) "5-day forecast" else "Ramalan 5 hari",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                TextButton(
                    onClick = onMoreDetailsClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (isEnglish) "More details ▸" else "Rincian lainnya ▸",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )
                }
            }

            // Days list
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                dailyItems.take(5).forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.weatherIcon,
                                fontSize = 22.sp
                            )
                            Text(
                                text = "${item.dayName}  ${item.description}",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Text(
                            text = "${item.tempMax.toInt()}° / ${item.tempMin.toInt()}°",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Bottom pill button
            Button(
                onClick = onMoreDetailsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isEnglish) "5-day forecast" else "Ramalan 5 hari",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
