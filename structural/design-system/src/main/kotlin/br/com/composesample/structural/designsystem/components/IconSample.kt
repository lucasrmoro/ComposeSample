package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.composesample.structural.designsystem.tokens.SampleColorToken
import br.com.composesample.structural.designsystem.tokens.SampleIcons

@Composable
fun IconSample(
    icon: SampleIcons,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: SampleColorToken
) {
    Icon(
        imageVector = icon.imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint.color()
    )
}
