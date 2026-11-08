package br.com.composesample.structural.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import br.com.composesample.structural.designsystem.tokens.SampleDimens
import br.com.composesample.structural.designsystem.tokens.SampleColor
import br.com.composesample.structural.designsystem.tokens.IconToken
import br.com.composesample.structural.designsystem.tokens.SampleIcons
import br.com.composesample.structural.designsystem.tokens.SampleTypography
import br.com.composesample.structural.designsystem.tokens.TypographyToken
import br.com.composesample.structural.designsystem.tokens.SamplePalette

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

private fun ColorScheme.toSampleColors() =
    ComposeSampleColors(
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
        val xxxxLarge get() = SampleDimens.Size.Size64
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
        val video get() = SampleDimens.Ratio.Video
    }

    object icon {
        val person: IconToken get() = SampleIcons.Person
        val search: IconToken get() = SampleIcons.Search
        val close: IconToken get() = SampleIcons.Close
    }

    object typography {
        val labelSmall: TypographyToken get() = SampleTypography.LabelSmall
        val labelMedium: TypographyToken get() = SampleTypography.LabelMedium
        val bodyLarge: TypographyToken get() = SampleTypography.BodyLarge
    }

    object colors {
        val onSurfaceVariant = SampleColor { MaterialTheme.colorScheme.onSurfaceVariant }
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
    SystemBarsSample(darkTheme = darkTheme)
    CompositionLocalProvider(LocalComposeSampleColors provides colorScheme.toSampleColors()) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}
