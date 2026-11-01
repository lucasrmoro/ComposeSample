package br.com.composesample.structural.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import br.com.composesample.structural.designsystem.tokens.Palette
import br.com.composesample.structural.designsystem.tokens.Dimen

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

private val LightColors = ComposeSampleColors(
    primary = Palette.Green.S700,
    onPrimary = Palette.White,
    secondary = Palette.Teal.S700,
    onSecondary = Palette.White,
    background = Palette.Neutral.S0,
    surface = Palette.Neutral.S0,
    onSurface = Palette.Neutral.S900,
    scrim = Palette.Neutral.Scrim
)

private val DarkColors = ComposeSampleColors(
    primary = Palette.Green.S300,
    onPrimary = Palette.Green.S900,
    secondary = Palette.Teal.S200,
    onSecondary = Palette.Teal.S900,
    background = Palette.Neutral.S950,
    surface = Palette.Neutral.S900,
    onSurface = Palette.Neutral.S0,
    scrim = Palette.Neutral.Scrim
)

private val LocalComposeSampleColors = staticCompositionLocalOf { LightColors }

object CurrentTheme {

    val size = Dimen.Size
    val spacing = Dimen.Spacing
    val stroke = Dimen.Stroke

    val color: ComposeSampleColors
        @Composable
        @ReadOnlyComposable
        get() = LocalComposeSampleColors.current
}

@Composable
fun ComposeSampleTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    CompositionLocalProvider(
        LocalComposeSampleColors provides colors
    ) {
        MaterialTheme(content = content)
    }
}
