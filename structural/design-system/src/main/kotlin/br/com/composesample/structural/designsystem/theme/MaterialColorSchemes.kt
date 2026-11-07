package br.com.composesample.structural.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import br.com.composesample.structural.designsystem.tokens.SamplePalette

internal val LightColorScheme =
    lightColorScheme(
        primary = SamplePalette.Light.Primary,
        onPrimary = SamplePalette.Light.OnPrimary,
        primaryContainer = SamplePalette.Light.PrimaryContainer,
        onPrimaryContainer = SamplePalette.Light.OnPrimaryContainer,
        secondary = SamplePalette.Light.Secondary,
        onSecondary = SamplePalette.Light.OnSecondary,
        secondaryContainer = SamplePalette.Light.SecondaryContainer,
        onSecondaryContainer = SamplePalette.Light.OnSecondaryContainer,
        background = SamplePalette.Light.Background,
        surface = SamplePalette.Light.Surface,
        onSurface = SamplePalette.Light.OnSurface,
        surfaceVariant = SamplePalette.Light.SurfaceVariant,
        onSurfaceVariant = SamplePalette.Light.OnSurfaceVariant,
        outline = SamplePalette.Light.Outline,
        outlineVariant = SamplePalette.Light.OutlineVariant,
        onBackground = SamplePalette.Light.OnSurface
    )

internal val DarkColorScheme =
    darkColorScheme(
        primary = SamplePalette.Dark.Primary,
        onPrimary = SamplePalette.Dark.OnPrimary,
        primaryContainer = SamplePalette.Dark.PrimaryContainer,
        onPrimaryContainer = SamplePalette.Dark.OnPrimaryContainer,
        secondary = SamplePalette.Dark.Secondary,
        onSecondary = SamplePalette.Dark.OnSecondary,
        secondaryContainer = SamplePalette.Dark.SecondaryContainer,
        onSecondaryContainer = SamplePalette.Dark.OnSecondaryContainer,
        background = SamplePalette.Dark.Background,
        surface = SamplePalette.Dark.Surface,
        onSurface = SamplePalette.Dark.OnSurface,
        surfaceVariant = SamplePalette.Dark.SurfaceVariant,
        onSurfaceVariant = SamplePalette.Dark.OnSurfaceVariant,
        outline = SamplePalette.Dark.Outline,
        outlineVariant = SamplePalette.Dark.OutlineVariant,
        onBackground = SamplePalette.Dark.OnSurface
    )
