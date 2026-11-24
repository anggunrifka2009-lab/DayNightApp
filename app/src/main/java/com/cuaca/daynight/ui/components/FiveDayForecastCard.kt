package com.cuaca.daynight.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (isDark) Color(0x66294D7A) else Color(0xDDFFFFFF)
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val subtextColor = if (isDark) Color.White.copy(alpha = 0.75f) else Color(0xFF64748B)
    val buttonBg = if (isDark) Color.White.copy(alpha = 0.2f) else Color(0xFF3B82F6).copy(alpha = 0.12f)
    val buttonContent = if (isDark) Color.White else Color(0xFF1D4ED8)

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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isEnglish) "5-day forecast" else "Ramalan 5 hari",
                        color = textColor,
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
                        color = subtextColor,
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
                                color = textColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Text(
                            text = "${item.tempMax.toInt()}° / ${item.tempMin.toInt()}°",
                            color = textColor,
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
                    containerColor = buttonBg,
                    contentColor = buttonContent
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
