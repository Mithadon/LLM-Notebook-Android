package com.llmnotebook.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

@Composable
fun ColorBox(
    color: Color,
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        Box(
            modifier = Modifier
                .size(120.dp, 40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            // Calculate perceived brightness using relative luminance formula
            val red = android.graphics.Color.red(color.toArgb()) / 255.0
            val green = android.graphics.Color.green(color.toArgb()) / 255.0
            val blue = android.graphics.Color.blue(color.toArgb()) / 255.0
            val luminance = 0.299 * red + 0.587 * green + 0.114 * blue

            Text(
                text = "#${color.toArgb().toUInt().toString(16).padStart(8, '0').uppercase()}",
                color = if (luminance > 0.5) Color.Black else Color.White
            )
        }
    }
}
