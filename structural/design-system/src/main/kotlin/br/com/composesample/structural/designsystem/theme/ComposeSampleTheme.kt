package br.com.composesample.structural.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import br.com.composesample.structural.designsystem.tokens.ColorToken
import br.com.composesample.structural.designsystem.tokens.IconToken
import br.com.composesample.structural.designsystem.tokens.SampleDimens
import br.com.composesample.structural.designsystem.tokens.SampleIcons
import br.com.composesample.structural.designsystem.tokens.SamplePalette
import br.com.composesample.structural.designsystem.tokens.TypographyToken

data class ComposeSampleColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val scrim: Color
)

private fun ColorScheme.toSampleColors() = ComposeSampleColors(
    primary = primary,
    onPrimary = onPrimary,
    secondary = secondary,
    onSecondary = onSecondary,
    background = background,
    surface = surface,
    onSurface = onSurface,
    scrim = SamplePalette.Neutral.Scrim
)

private val LightColors = LightColorScheme.toSampleColors()

private val LocalComposeSampleColors = staticCompositionLocalOf { LightColors }

object CurrentTheme {

    object size {
        val xxxSmall get() = SampleDimens.Size.Size4
        val xxSmall get() = SampleDimens.Size.Size8
        val xSmall get() = SampleDimens.Size.Size12
        val small get() = SampleDimens.Size.Size16
        val medium get() = SampleDimens.Size.Size20
        val large get() = SampleDimens.Size.Size24
        val xLarge get() = SampleDimens.Size.Size32
        val xxLarge get() = SampleDimens.Size.Size40
        val xxxLarge get() = SampleDimens.Size.Size48
        val huge get() = SampleDimens.Size.Size64
    }

    object spacing {
        val xSmall get() = SampleDimens.Spacing.XSmall
        val small get() = SampleDimens.Spacing.Small
        val medium get() = SampleDimens.Spacing.Medium
        val large get() = SampleDimens.Spacing.Large
        val xLarge get() = SampleDimens.Spacing.XLarge
    }

    object stroke {
        val thin get() = SampleDimens.Stroke.Thin
    }

    object ratio {
        val video get() = SampleDimens.Ratio.VIDEO
    }

    object icon {
        val add: IconToken get() = SampleIcons.Add
        val star: IconToken get() = SampleIcons.Star
        val person: IconToken get() = SampleIcons.Person
        val search: IconToken get() = SampleIcons.Search
        val close: IconToken get() = SampleIcons.Close
    }

    object typography {
        val labelSmall = TypographyToken(
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = SampleDimens.FontSize.LabelSmall,
                lineHeight = SampleDimens.LineHeight.Label,
                letterSpacing = SampleDimens.LetterSpacing.Label
            )
        )
        val labelMedium = TypographyToken(
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = SampleDimens.FontSize.LabelMedium,
                lineHeight = SampleDimens.LineHeight.Label,
                letterSpacing = SampleDimens.LetterSpacing.Label
            )
        )
        val bodyLarge = TypographyToken(
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = SampleDimens.FontSize.BodyLarge,
                lineHeight = SampleDimens.LineHeight.Body,
                letterSpacing = SampleDimens.LetterSpacing.Body
            )
        )
        val headlineSmall = TypographyToken(
            TextStyle(
                fontSize = SampleDimens.FontSize.HeadlineSmall,
                fontWeight = FontWeight.Normal
            )
        )
        val titleLarge = TypographyToken(
            TextStyle(
                fontSize = SampleDimens.FontSize.TitleLarge,
                fontWeight = FontWeight.Normal
            )
        )
        val titleSmall = TypographyToken(
            TextStyle(
                fontSize = SampleDimens.FontSize.TitleSmall,
                fontWeight = FontWeight.Medium
            )
        )
        val bodyMedium = TypographyToken(
            TextStyle(
                fontSize = SampleDimens.FontSize.BodyMedium,
                fontWeight = FontWeight.Normal
            )
        )
        val bodySmall = TypographyToken(
            TextStyle(
                fontSize = SampleDimens.FontSize.BodySmall,
                fontWeight = FontWeight.Normal
            )
        )
    }

    object colors {
        val primary = ColorToken { CurrentTheme.color.primary }
        val shimmer = ColorToken { MaterialTheme.colorScheme.outlineVariant }
        val onSurfaceVariant = ColorToken { MaterialTheme.colorScheme.onSurfaceVariant }
    }

    val color: ComposeSampleColors
        @Composable @ReadOnlyComposable get() = LocalComposeSampleColors.current

}

@Composable
fun ComposeSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    SampleSystemBars(darkTheme = darkTheme)
    CompositionLocalProvider(LocalComposeSampleColors provides colorScheme.toSampleColors()) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}
