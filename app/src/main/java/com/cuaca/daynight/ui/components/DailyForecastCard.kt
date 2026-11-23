package com.cuaca.daynight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun DailyForecastCard(
    day: String,
    weatherIcon: String,
    description: String,
    temperatureMin: Double,
    temperatureMax: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = day,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = weatherIcon,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${temperatureMin.toInt()}° / ${temperatureMax.toInt()}°",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun DailyForecastPreviewContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Prakiraan 7 Hari",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )

        DailyForecastCard(
            day = "Senin",
            weatherIcon = "☀️",
            description = "Cerah",
            temperatureMin = 23.0,
            temperatureMax = 30.0
        )

        DailyForecastCard(
            day = "Selasa",
            weatherIcon = "🌤️",
            description = "Cerah berawan",
            temperatureMin = 24.0,
            temperatureMax = 31.0
        )

        DailyForecastCard(
            day = "Rabu",
            weatherIcon = "🌧️",
            description = "Hujan",
            temperatureMin = 22.0,
            temperatureMax = 28.0
        )

        DailyForecastCard(
            day = "Kamis",
            weatherIcon = "⛈️",
            description = "Badai petir",
            temperatureMin = 21.0,
            temperatureMax = 27.0
        )
    }
}

@Preview(showBackground = true, name = "Daily Forecast - Light")
@Composable
private fun DailyForecastLightPreview() {
    DayNightTheme(darkTheme = false) {
        DailyForecastPreviewContent()
    }
}

@Preview(showBackground = true, name = "Daily Forecast - Dark")
@Composable
private fun DailyForecastDarkPreview() {
    DayNightTheme(darkTheme = true) {
        DailyForecastPreviewContent()
    }
}
