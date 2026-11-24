package com.cuaca.daynight.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cuaca.daynight.data.preferences.SavedCity
import com.cuaca.daynight.viewmodel.WeatherUiState

@Composable
fun ManageCitiesScreen(
    savedCities: List<SavedCity>,
    currentCityName: String,
    weatherStates: Map<String, WeatherUiState> = emptyMap(),
    isEnglish: Boolean = false,
    onBackClick: () -> Unit,
    onCitySelected: (SavedCity) -> Unit,
    onAddCity: (String) -> Unit,
    onDeleteCity: (SavedCity) -> Unit
) {
    var searchInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = if (isEnglish) "Back" else "Kembali",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isEnglish) "Manage cities" else "Kelola kota",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Search bar
        OutlinedTextField(
            value = searchInput,
            onValueChange = { searchInput = it },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            placeholder = {
                Text(
                    text = if (isEnglish) "Enter location" else "Masukkan lokasi",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = if (isEnglish) "Search" else "Cari",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchInput.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onAddCity(searchInput.trim())
                            searchInput = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = if (isEnglish) "Add" else "Tambah",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            singleLine = true
        )

        // Saved cities list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(
                items = savedCities,
                key = { _, city -> city.name }
            ) { index, city ->
                val isCurrentLocation = index == 0

                val containerColor = if (isCurrentLocation) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }

                val contentColor = if (isCurrentLocation) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                val weatherState = weatherStates[city.name]
                val currentTemp = weatherState?.weather?.current?.temperature?.toInt()
                val tempMax = weatherState?.weather?.daily?.temperatureMax?.firstOrNull()?.toInt()
                val tempMin = weatherState?.weather?.daily?.temperatureMin?.firstOrNull()?.toInt()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCitySelected(city) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = containerColor
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = city.name,
                                    color = contentColor,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (isCurrentLocation) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = if (isEnglish) "Current Location" else "Lokasi saat ini",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            if (isCurrentLocation) {
                                Text(
                                    text = if (isEnglish) "Current Location" else "Lokasi saat ini",
                                    color = contentColor.copy(alpha = 0.75f),
                                    fontSize = 13.sp
                                )
                            } else if (tempMax != null && tempMin != null) {
                                Text(
                                    text = "$tempMax° / $tempMin°",
                                    color = contentColor.copy(alpha = 0.75f),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (currentTemp != null) {
                                Text(
                                    text = "$currentTemp°",
                                    color = contentColor,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }

                            // Lokasi saat ini (index == 0) TIDAK BISA DIHAPUS
                            if (!isCurrentLocation) {
                                IconButton(
                                    onClick = { onDeleteCity(city) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = if (isEnglish) "Delete" else "Hapus",
                                        tint = contentColor.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
