package com.cuaca.daynight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cuaca.daynight.data.preferences.ThemeMode
import com.cuaca.daynight.data.preferences.ThemePreferences
import com.cuaca.daynight.ui.screen.HomeScreen
import com.cuaca.daynight.ui.theme.DayNightTheme
import com.cuaca.daynight.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val themePreferences = remember {
                ThemePreferences(applicationContext)
            }

            val themeMode by themePreferences.themeMode.collectAsState(
                initial = ThemeMode.SYSTEM
            )

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            DayNightTheme(darkTheme = darkTheme) {

                val weatherViewModel: WeatherViewModel = viewModel()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(viewModel = weatherViewModel)
                }
            }
        }
    }
}
