package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.composesample.structural.designsystem.tokens.SampleColor
import br.com.composesample.structural.designsystem.tokens.SampleIcon

@Composable
fun IconSample(
    modifier: Modifier = Modifier,
    icon: SampleIcon,
    contentDescription: String?,
    tint: SampleColor
) {
    Icon(
        imageVector = icon.imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint.color()
    )
}
