package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.composesample.structural.designsystem.tokens.SampleColors
import br.com.composesample.structural.designsystem.tokens.SampleIcons

@Composable
fun IconSample(
    modifier: Modifier = Modifier,
    icon: SampleIcons,
    contentDescription: String?,
    tint: SampleColors
) {
    val tintColor =
        when (tint) {
            SampleColors.OnSurfaceVariant -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    Icon(
        imageVector = icon.imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tintColor
    )
}
