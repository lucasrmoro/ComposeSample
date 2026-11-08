package br.com.composesample.structural.designsystem.tokens

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

class TypographyToken internal constructor(internal val textStyle: TextStyle)

internal object SampleTypography {
    val LabelSmall = TypographyToken(
        TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = SampleDimens.Text.LabelSmall,
            lineHeight = SampleDimens.Text.LabelLineHeight,
            letterSpacing = SampleDimens.Text.LabelLetterSpacing
        )
    )
    val LabelMedium = TypographyToken(
        TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = SampleDimens.Text.LabelMedium,
            lineHeight = SampleDimens.Text.LabelLineHeight,
            letterSpacing = SampleDimens.Text.LabelLetterSpacing
        )
    )
    val BodyLarge = TypographyToken(
        TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = SampleDimens.Text.BodyLarge,
            lineHeight = SampleDimens.Text.BodyLineHeight,
            letterSpacing = SampleDimens.Text.BodyLetterSpacing
        )
    )
}
