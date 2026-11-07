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
import br.com.composesample.structural.designsystem.tokens.SampleColors

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
        scrim = SampleColors.Neutral.Scrim
    )

private val LightColors = LightColorScheme.toSampleColors()

private val SampleTypography =
    Typography().let { typography ->
        Typography(titleLarge = typography.titleLarge.copy(fontSize = SampleDimens.Text.SectionTitle))
    }

private val LocalComposeSampleColors = staticCompositionLocalOf { LightColors }

object CurrentTheme {

    val size = SampleDimens.Size
    val spacing = SampleDimens.Spacing
    val stroke = SampleDimens.Stroke

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
