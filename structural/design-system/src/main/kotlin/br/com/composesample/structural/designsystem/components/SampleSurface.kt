package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme

/** Provides a themed surface or clickable card for composing feature-specific content. */
@Composable
fun SampleSurface(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    outlined: Boolean = false,
    shape: Shape = RoundedCornerShape(CurrentTheme.size.small),
    content: @Composable () -> Unit
) {
    val border = if (outlined) BorderStroke(CurrentTheme.stroke.thin, CurrentTheme.color.onSurface)
    else null
    if (onClick == null) Surface(
        modifier = modifier,
        shape = shape,
        border = border,
        content = content
    ) else Card(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        border = border,
        colors = CardDefaults.cardColors(containerColor = CurrentTheme.color.surface),
        content = { content() }
    )
}

@Preview(showBackground = true)
@Composable
private fun SampleSurfacePreview() {
    ComposeSampleTheme {
        SampleSurface(outlined = true) {
            SampleText(modifier = Modifier, text = "Sample content")
        }
    }
}
