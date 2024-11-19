package com.llmnotebook.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.ui.components.ColorBox
import com.llmnotebook.app.util.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ThemeColorsSection(
    colorPrimary: Color,
    colorOnPrimary: Color,
    colorSecondary: Color,
    colorOnSecondary: Color,
    colorBackground: Color,
    colorOnBackground: Color,
    colorSurface: Color,
    colorOnSurface: Color,
    settingsManager: SettingsManager,
    scope: CoroutineScope,
    onShowColorPickerChange: (Boolean) -> Unit,
    onCurrentColorTitleChange: (String) -> Unit,
    onCurrentColorChange: (Color) -> Unit,
    onColorSelectedChange: ((Color) -> Unit) -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Theme Colors",
                style = MaterialTheme.typography.titleMedium
            )

            ColorBox(
                color = colorPrimary,
                name = "Primary",
                onClick = {
                    onCurrentColorTitleChange("Primary Color")
                    onCurrentColorChange(colorPrimary)
                    onColorSelectedChange { scope.launch { settingsManager.setColorPrimary(it) } }
                    onShowColorPickerChange(true)
                }
            )

            ColorBox(
                color = colorOnPrimary,
                name = "On Primary",
                onClick = {
                    onCurrentColorTitleChange("On Primary Color")
                    onCurrentColorChange(colorOnPrimary)
                    onColorSelectedChange { scope.launch { settingsManager.setColorOnPrimary(it) } }
                    onShowColorPickerChange(true)
                }
            )

            ColorBox(
                color = colorSecondary,
                name = "Secondary",
                onClick = {
                    onCurrentColorTitleChange("Secondary Color")
                    onCurrentColorChange(colorSecondary)
                    onColorSelectedChange { scope.launch { settingsManager.setColorSecondary(it) } }
                    onShowColorPickerChange(true)
                }
            )

            ColorBox(
                color = colorOnSecondary,
                name = "On Secondary",
                onClick = {
                    onCurrentColorTitleChange("On Secondary Color")
                    onCurrentColorChange(colorOnSecondary)
                    onColorSelectedChange { scope.launch { settingsManager.setColorOnSecondary(it) } }
                    onShowColorPickerChange(true)
                }
            )

            ColorBox(
                color = colorBackground,
                name = "Background",
                onClick = {
                    onCurrentColorTitleChange("Background Color")
                    onCurrentColorChange(colorBackground)
                    onColorSelectedChange { scope.launch { settingsManager.setColorBackground(it) } }
                    onShowColorPickerChange(true)
                }
            )

            ColorBox(
                color = colorOnBackground,
                name = "On Background",
                onClick = {
                    onCurrentColorTitleChange("On Background Color")
                    onCurrentColorChange(colorOnBackground)
                    onColorSelectedChange { scope.launch { settingsManager.setColorOnBackground(it) } }
                    onShowColorPickerChange(true)
                }
            )

            ColorBox(
                color = colorSurface,
                name = "Surface",
                onClick = {
                    onCurrentColorTitleChange("Surface Color")
                    onCurrentColorChange(colorSurface)
                    onColorSelectedChange { scope.launch { settingsManager.setColorSurface(it) } }
                    onShowColorPickerChange(true)
                }
            )

            ColorBox(
                color = colorOnSurface,
                name = "On Surface",
                onClick = {
                    onCurrentColorTitleChange("On Surface Color")
                    onCurrentColorChange(colorOnSurface)
                    onColorSelectedChange { scope.launch { settingsManager.setColorOnSurface(it) } }
                    onShowColorPickerChange(true)
                }
            )
        }
    }
}
