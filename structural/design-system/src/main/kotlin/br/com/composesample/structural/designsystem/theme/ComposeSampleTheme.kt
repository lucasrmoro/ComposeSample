package br.com.composesample.structural.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import br.com.composesample.structural.designsystem.tokens.SampleDimens
import br.com.composesample.structural.designsystem.tokens.SampleColor
import br.com.composesample.structural.designsystem.tokens.IconToken
import br.com.composesample.structural.designsystem.tokens.SampleIcons
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

private val SampleTypography =
    Typography().let { typography ->
        Typography(titleLarge = typography.titleLarge.copy(fontSize = SampleDimens.Text.SectionTitle))
    }

private val LocalComposeSampleColors = staticCompositionLocalOf { LightColors }

object CurrentTheme {

    object size {
        val Small get() = SampleDimens.Size.Small
        val Medium get() = SampleDimens.Size.Medium
        val FloatingActionClearance get() = SampleDimens.Size.FloatingActionClearance
        val TouchTarget get() = SampleDimens.Size.TouchTarget
        val PosterWidth get() = SampleDimens.Size.PosterWidth
        val PosterHeight get() = SampleDimens.Size.PosterHeight
        val BackdropHeight get() = SampleDimens.Size.BackdropHeight
        val Portrait get() = SampleDimens.Size.Portrait
        val RatingIcon get() = SampleDimens.Size.RatingIcon
    }

    object spacing {
        val ExtraSmall get() = SampleDimens.Spacing.ExtraSmall
        val Small get() = SampleDimens.Spacing.Small
        val Medium get() = SampleDimens.Spacing.Medium
        val Large get() = SampleDimens.Spacing.Large
        val ExtraLarge get() = SampleDimens.Spacing.ExtraLarge
    }

    object stroke {
        val Thin get() = SampleDimens.Stroke.Thin
    }

    object icon {
        val Person: IconToken get() = SampleIcons.Person
    }

    object colors {
        val OnSurfaceVariant = SampleColor { MaterialTheme.colorScheme.onSurfaceVariant }
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
        MaterialTheme(colorScheme = colorScheme, typography = SampleTypography, content = content)
    }
}
