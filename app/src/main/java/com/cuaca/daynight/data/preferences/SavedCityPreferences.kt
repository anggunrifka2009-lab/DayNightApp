package com.cuaca.daynight.data.preferences

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class SavedCity(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

class SavedCityPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(
        "saved_cities",
        Context.MODE_PRIVATE
    )

    fun getSavedCities(): List<SavedCity> {
        val json = preferences.getString("cities", null)
            ?: return emptyList()

        return try {
            val jsonArray = JSONArray(json)

            List(jsonArray.length()) { index ->
                val city = jsonArray.getJSONObject(index)

                SavedCity(
                    name = city.getString("name"),
                    latitude = city.getDouble("latitude"),
                    longitude = city.getDouble("longitude")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveCities(cities: List<SavedCity>) {
        val jsonArray = JSONArray()

        cities.forEach { city ->
            val jsonObject = JSONObject().apply {
                put("name", city.name)
                put("latitude", city.latitude)
                put("longitude", city.longitude)
            }

            jsonArray.put(jsonObject)
        }

        preferences.edit()
            .putString("cities", jsonArray.toString())
            .apply()
    }
}
