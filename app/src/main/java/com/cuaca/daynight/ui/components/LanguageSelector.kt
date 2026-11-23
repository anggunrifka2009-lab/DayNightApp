package com.cuaca.daynight.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cuaca.daynight.data.preferences.AppLanguage
import com.cuaca.daynight.ui.theme.DayNightTheme

@Composable
fun LanguageSelector(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = listOf(
        AppLanguage.INDONESIAN to "Indonesia",
        AppLanguage.ENGLISH to "English"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = when (selectedLanguage) {
                AppLanguage.INDONESIAN -> "Bahasa"
                AppLanguage.ENGLISH -> "Language"
            },
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            languages.forEachIndexed { index, (language, label) ->
                SegmentedButton(
                    selected = selectedLanguage == language,
                    onClick = {
                        onLanguageSelected(language)
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = languages.size
                    )
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Bahasa Indonesia")
@Composable
private fun LanguageSelectorPreview() {
    DayNightTheme {
        LanguageSelector(
            selectedLanguage = AppLanguage.INDONESIAN,
            onLanguageSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "English")
@Composable
private fun LanguageSelectorEnglishPreview() {
    DayNightTheme {
        LanguageSelector(
            selectedLanguage = AppLanguage.ENGLISH,
            onLanguageSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
