package com.cuaca.daynight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp

@Composable
fun WeatherCard(
    city: String,
    temperature: Double,
    description: String,
    weatherIcon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = city,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = weatherIcon,
                fontSize = 64.sp
            )

            Text(
                text = "${temperature.toInt()}°",
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                text = description,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Weather Card Preview"
)
@Composable
fun WeatherCardPreview() {
    MaterialTheme {
        WeatherCard(
            city = "Yogyakarta",
            temperature = 29.0,
            description = "Cerah berawan",
            weatherIcon = "🌤️"
        )
    }
}
