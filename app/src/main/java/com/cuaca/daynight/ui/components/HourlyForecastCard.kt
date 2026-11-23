package com.cuaca.daynight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HourlyForecastCard(
    time: String,
    temperature: Double,
    weatherIcon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(90.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = weatherIcon,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "${temperature.toInt()}°",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Hourly Forecast Preview"
)
@Composable
fun HourlyForecastCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Prakiraan Per Jam",
                style = MaterialTheme.typography.titleLarge
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    HourlyForecastCard(
                        time = "10:00",
                        temperature = 29.0,
                        weatherIcon = "☀️"
                    )
                }

                item {
                    HourlyForecastCard(
                        time = "11:00",
                        temperature = 30.0,
                        weatherIcon = "🌤️"
                    )
                }

                item {
                    HourlyForecastCard(
                        time = "12:00",
                        temperature = 31.0,
                        weatherIcon = "☁️"
                    )
                }

                item {
                    HourlyForecastCard(
                        time = "13:00",
                        temperature = 28.0,
                        weatherIcon = "🌧️"
                    )
                }
            }
        }
    }
}
