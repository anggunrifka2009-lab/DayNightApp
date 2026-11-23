package com.cuaca.daynight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.cuaca.daynight.ui.theme.DayNightTheme

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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = weatherIcon,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "${temperature.toInt()}°",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private data class HourlyPreviewData(
    val time: String,
    val temperature: Double,
    val icon: String
)

@Composable
private fun HourlyForecastPreviewContent() {
    val forecasts = listOf(
        HourlyPreviewData("10:00", 29.0, "☀️"),
        HourlyPreviewData("11:00", 30.0, "🌤️"),
        HourlyPreviewData("12:00", 31.0, "☁️"),
        HourlyPreviewData("13:00", 28.0, "🌧️")
    )

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Prakiraan Per Jam",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(forecasts) { forecast ->
                HourlyForecastCard(
                    time = forecast.time,
                    temperature = forecast.temperature,
                    weatherIcon = forecast.icon
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Hourly Forecast - Light")
@Composable
private fun HourlyForecastLightPreview() {
    DayNightTheme(darkTheme = false) {
        HourlyForecastPreviewContent()
    }
}

@Preview(showBackground = true, name = "Hourly Forecast - Dark")
@Composable
private fun HourlyForecastDarkPreview() {
    DayNightTheme(darkTheme = true) {
        HourlyForecastPreviewContent()
    }
}
