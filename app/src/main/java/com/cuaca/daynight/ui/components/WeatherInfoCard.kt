package com.cuaca.daynight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun WeatherInfoCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = icon)

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Weather Info Card Preview"
)
@Composable
fun WeatherInfoCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeatherInfoCard(
                title = "Kelembapan",
                value = "75%",
                icon = "💧",
                modifier = Modifier.fillMaxWidth()
            )

            WeatherInfoCard(
                title = "Kecepatan Angin",
                value = "12 km/j",
                icon = "💨",
                modifier = Modifier.fillMaxWidth()
            )

            WeatherInfoCard(
                title = "Kemungkinan Hujan",
                value = "40%",
                icon = "🌧️",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
