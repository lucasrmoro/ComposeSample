package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.composesample.structural.designsystem.tokens.ColorToken
import br.com.composesample.structural.designsystem.tokens.IconToken

@Composable
fun SampleIcon(
    modifier: Modifier = Modifier,
    icon: IconToken,
    contentDescription: String?,
    tint: ColorToken
) {
    Icon(
        imageVector = icon.imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint.color()
    )
}
