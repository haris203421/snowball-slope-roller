package com.flurry.snowballslope.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.flurry.snowballslope.core.theme.FrostSurface
import com.flurry.snowballslope.core.theme.GlacierPrimary
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.PureSnow

data class ChoiceOption(val id: String, val label: String)

@Composable
fun SegmentedChoice(
    options: List<ChoiceOption>,
    selectedId: String,
    selectedState: String,
    idleState: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(FrostSurface.copy(alpha = 0.5f))
            .padding(4.dp)
    ) {
        options.forEach { option ->
            val active = option.id == selectedId
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (active) GlacierPrimary else FrostSurface.copy(alpha = 0f))
                    .clickable { onSelect(option.id) }
                    .semantics { stateDescription = if (active) selectedState else idleState },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (active) PureSnow else NightSlope
                )
            }
        }
    }
}
