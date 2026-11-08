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
            fontSize = SampleDimens.FontSize.LabelSmall,
            lineHeight = SampleDimens.LineHeight.Label,
            letterSpacing = SampleDimens.LetterSpacing.Label
        )
    )
    val LabelMedium = TypographyToken(
        TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = SampleDimens.FontSize.LabelMedium,
            lineHeight = SampleDimens.LineHeight.Label,
            letterSpacing = SampleDimens.LetterSpacing.Label
        )
    )
    val BodyLarge = TypographyToken(
        TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = SampleDimens.FontSize.BodyLarge,
            lineHeight = SampleDimens.LineHeight.Body,
            letterSpacing = SampleDimens.LetterSpacing.Body
        )
    )
}
