package com.flurry.snowballslope.presentation.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flurry.snowballslope.R
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.PureSnow
import com.flurry.snowballslope.core.theme.ScarfRed
import com.flurry.snowballslope.core.theme.SlateText

@Composable
fun ResetDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        shape = RoundedCornerShape(24.dp),
        containerColor = PureSnow,
        title = {
            Text(
                text = stringResource(R.string.reset_progress),
                style = MaterialTheme.typography.titleLarge,
                color = NightSlope
            )
        },
        text = {
            Text(
                text = stringResource(R.string.reset_question),
                style = MaterialTheme.typography.bodyLarge,
                color = SlateText
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.reset),
                    style = MaterialTheme.typography.titleMedium,
                    color = ScarfRed
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.titleMedium,
                    color = NightSlope
                )
            }
        }
    )
}
