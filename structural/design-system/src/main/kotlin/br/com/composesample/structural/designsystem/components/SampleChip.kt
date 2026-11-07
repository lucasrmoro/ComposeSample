package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.composesample.structural.designsystem.tokens.SampleDimens

/** Reusable chip for an action with a short label. */
@Composable
fun SampleChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.height(SampleDimens.Size.TouchTarget),
        contentAlignment = Alignment.CenterStart
    ) {
        AssistChip(onClick = onClick, label = { Text(label) })
    }
}
