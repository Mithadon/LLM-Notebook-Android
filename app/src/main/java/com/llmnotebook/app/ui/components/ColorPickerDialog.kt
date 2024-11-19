package com.llmnotebook.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit,
    title: String
) {
    var currentHue by remember { mutableStateOf(0f) }
    var currentSaturation by remember { mutableStateOf(0f) }
    var currentValue by remember { mutableStateOf(0f) }
    var currentAlpha by remember { mutableStateOf(1f) }

    // Initialize color values
    LaunchedEffect(initialColor) {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(initialColor.toArgb(), hsv)
        currentHue = hsv[0]
        currentSaturation = hsv[1]
        currentValue = hsv[2]
        currentAlpha = initialColor.alpha
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Color preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color.Transparent, Color.Black),
                                start = Offset(0f, 0f),
                                end = Offset(0f, Float.POSITIVE_INFINITY)
                            )
                        )
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                ) {
                    val currentColor = Color.hsv(currentHue, currentSaturation, currentValue, currentAlpha)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(currentColor)
                    )
                    Text(
                        text = "#${currentColor.toArgb().toUInt().toString(16).padStart(8, '0').uppercase()}",
                        color = if (currentValue > 0.5f) Color.Black else Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp)
                    )
                }

                // Hue wheel
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val center = size.width / 2f
                                    val dx = offset.x - center
                                    val dy = offset.y - center
                                    val radius = hypot(dx, dy)
                                    if (radius <= center) {
                                        val angle = (atan2(dy, dx) * 180f / PI).toFloat()
                                        currentHue = (angle + 360f) % 360f
                                    }
                                }
                            }
                    ) {
                        val radius = size.width / 2f
                        for (angle in 0..360) {
                            val color = Color.hsv(angle.toFloat(), 1f, 1f)
                            drawLine(
                                brush = SolidColor(color),
                                start = center + Offset(
                                    (radius * 0.8f * kotlin.math.cos(angle * PI / 180f)).toFloat(),
                                    (radius * 0.8f * kotlin.math.sin(angle * PI / 180f)).toFloat()
                                ),
                                end = center + Offset(
                                    (radius * kotlin.math.cos(angle * PI / 180f)).toFloat(),
                                    (radius * kotlin.math.sin(angle * PI / 180f)).toFloat()
                                ),
                                strokeWidth = 2f
                            )
                        }
                    }
                }

                // Saturation slider
                Text("Saturation")
                Slider(
                    value = currentSaturation,
                    onValueChange = { currentSaturation = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )

                // Value slider
                Text("Value")
                Slider(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )

                // Alpha slider
                Text("Opacity")
                Slider(
                    value = currentAlpha,
                    onValueChange = { currentAlpha = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onColorSelected(Color.hsv(currentHue, currentSaturation, currentValue, currentAlpha))
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun Color.Companion.hsv(hue: Float, saturation: Float, value: Float, alpha: Float = 1f): Color {
    val hsv = FloatArray(3)
    hsv[0] = hue
    hsv[1] = saturation
    hsv[2] = value
    return Color(android.graphics.Color.HSVToColor((alpha * 255).roundToInt(), hsv))
}
