package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.structural.designsystem.tokens.TypographyToken

@Composable
fun SampleText(
    modifier: Modifier = Modifier,
    text: String,
    style: TypographyToken = CurrentTheme.typography.bodyLarge,
    color: Color = Color.Unspecified
) {
    Text(text = text, modifier = modifier, style = style.textStyle, color = color)
}

@Preview(showBackground = true)
@Composable
private fun SampleTextPreview() {
    ComposeSampleTheme {
        Column(
            modifier = Modifier.padding(CurrentTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small)
        ) {
            val styles = listOf(
                CurrentTheme.typography.labelSmall,
                CurrentTheme.typography.labelMedium,
                CurrentTheme.typography.bodyLarge
            )
            for (style in styles) {
                SampleText(text = stringResource(R.string.sample_typography_preview_text), style = style)
            }
        }
    }
}
