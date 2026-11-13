package com.flurry.snowballslope.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flurry.snowballslope.core.theme.FrostSurface
import com.flurry.snowballslope.core.theme.IceEdge
import com.flurry.snowballslope.core.theme.NightSlope

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val tint = if (enabled) 1f else 0.45f
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, IceEdge.copy(alpha = tint)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = FrostSurface.copy(alpha = 0.45f * tint),
            contentColor = NightSlope,
            disabledContainerColor = FrostSurface.copy(alpha = 0.2f),
            disabledContentColor = NightSlope.copy(alpha = 0.45f)
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
