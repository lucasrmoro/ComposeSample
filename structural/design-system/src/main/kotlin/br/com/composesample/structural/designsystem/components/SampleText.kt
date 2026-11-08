package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

enum class SampleTextStyle {
    BodyLarge,
    LabelMedium,
    LabelSmall
}

@Composable
fun SampleText(
    modifier: Modifier = Modifier,
    text: String,
    style: SampleTextStyle = SampleTextStyle.BodyLarge,
    color: Color = Color.Unspecified
) {
    val textStyle =
        when (style) {
            SampleTextStyle.BodyLarge -> MaterialTheme.typography.bodyLarge
            SampleTextStyle.LabelMedium -> MaterialTheme.typography.labelMedium
            SampleTextStyle.LabelSmall -> MaterialTheme.typography.labelSmall
        }
    Text(text = text, modifier = modifier, style = textStyle, color = color)
}
