package com.cuaca.daynight.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cuaca.daynight.data.preferences.SavedCity

@Composable
fun ManageCitiesScreen(
    savedCities: List<SavedCity>,
    currentCityName: String,
    isEnglish: Boolean = false,
    onBackClick: () -> Unit,
    onCitySelected: (SavedCity) -> Unit,
    onAddCity: (String) -> Unit,
    onDeleteCity: (SavedCity) -> Unit
) {
    var searchInput by remember { mutableStateOf("") }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF161C24),
            Color(0xFF0F141A),
            Color(0xFF0D1016)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Text(
                    text = "←",
                    color = Color.White,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isEnglish) "Manage cities" else "Kelola kota",
                color = Color.White,
                fontSize = 26.sp,
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
                focusedContainerColor = Color(0xFF1E2630),
                unfocusedContainerColor = Color(0xFF1E2630),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            placeholder = {
                Text(
                    text = if (isEnglish) "Enter location" else "Masukkan lokasi",
                    color = Color.White.copy(alpha = 0.45f)
                )
            },
            leadingIcon = {
                Text("🔍", fontSize = 16.sp)
            },
            trailingIcon = {
                if (searchInput.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onAddCity(searchInput.trim())
                            searchInput = ""
                        }
                    ) {
                        Text("➕", color = Color.White)
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
            items(
                items = savedCities,
                key = { it.name }
            ) { city ->
                val isCurrent = city.name.equals(currentCityName, ignoreCase = true)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCitySelected(city) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) Color(0xFF5394E6) else Color(0x994874AA)
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
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = city.name,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (isCurrent) {
                                    Text("📍", fontSize = 16.sp)
                                }
                            }

                            Text(
                                text = "${if (isEnglish) "AQI" else "IKU"} 62   33° / 24°",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "26°",
                                color = Color.White,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Light
                            )

                            IconButton(
                                onClick = { onDeleteCity(city) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Text("✕", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
