package com.flurry.snowballslope.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flurry.snowballslope.core.theme.IceEdge
import com.flurry.snowballslope.core.theme.PureSnow

@Composable
fun SnowCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(1.dp, IceEdge.copy(alpha = 0.6f)),
        colors = CardDefaults.cardColors(containerColor = PureSnow.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}
