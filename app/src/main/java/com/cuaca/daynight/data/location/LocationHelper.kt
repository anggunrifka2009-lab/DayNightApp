package com.cuaca.daynight.data.location

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class CitySearchResult(
    val name: String,
    val region: String,
    val latitude: Double,
    val longitude: Double
)

class LocationHelper(private val context: Context) {

    suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): String {

        if (!Geocoder.isPresent()) {
            return "Lokasi Saya"
        }

        return suspendCancellableCoroutine { continuation ->
            val geocoder = Geocoder(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    val address = addresses.firstOrNull()

                    val locationName = address?.subLocality
                        ?: address?.locality
                        ?: address?.subAdminArea
                        ?: address?.adminArea
                        ?: "Lokasi Saya"

                    if (continuation.isActive) {
                        continuation.resume(locationName)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                try {
                    val address = geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1
                    )?.firstOrNull()

                    val locationName = address?.subLocality
                        ?: address?.locality
                        ?: address?.subAdminArea
                        ?: address?.adminArea
                        ?: "Lokasi Saya"

                    if (continuation.isActive) {
                        continuation.resume(locationName)
                    }
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resume("Lokasi Saya")
                    }
                }
            }
        }
    }

    suspend fun getCoordinatesByCity(
        cityName: String
    ): Pair<Double, Double>? {

        if (!Geocoder.isPresent()) return null

        return suspendCancellableCoroutine { continuation ->
            val geocoder = Geocoder(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(
                    cityName,
                    1
                ) { addresses ->

                    val address = addresses.firstOrNull()

                    val coordinates = address?.let {
                        Pair(it.latitude, it.longitude)
                    }

                    if (continuation.isActive) {
                        continuation.resume(coordinates)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                try {
                    val address = geocoder.getFromLocationName(
                        cityName,
                        1
                    )?.firstOrNull()

                    val coordinates = address?.let {
                        Pair(it.latitude, it.longitude)
                    }

                    if (continuation.isActive) {
                        continuation.resume(coordinates)
                    }
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            }
        }
    }

    suspend fun searchCitySuggestions(query: String): List<CitySearchResult> {
        if (!Geocoder.isPresent() || query.isBlank()) return emptyList()

        return suspendCancellableCoroutine { continuation ->
            val geocoder = Geocoder(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(query, 5) { addresses ->
                    val results = addresses.mapNotNull { address ->
                        val cityName = address.locality ?: address.subAdminArea ?: address.featureName ?: query
                        val regionParts = listOfNotNull(address.adminArea, address.countryName).joinToString(", ")
                        CitySearchResult(
                            name = cityName,
                            region = regionParts,
                            latitude = address.latitude,
                            longitude = address.longitude
                        )
                    }.distinctBy { it.name.lowercase() }

                    if (continuation.isActive) {
                        continuation.resume(results)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                try {
                    val addresses = geocoder.getFromLocationName(query, 5) ?: emptyList()
                    val results = addresses.mapNotNull { address ->
                        val cityName = address.locality ?: address.subAdminArea ?: address.featureName ?: query
                        val regionParts = listOfNotNull(address.adminArea, address.countryName).joinToString(", ")
                        CitySearchResult(
                            name = cityName,
                            region = regionParts,
                            latitude = address.latitude,
                            longitude = address.longitude
                        )
                    }.distinctBy { it.name.lowercase() }

                    if (continuation.isActive) {
                        continuation.resume(results)
                    }
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resume(emptyList())
                    }
                }
            }
        }
    }
}
