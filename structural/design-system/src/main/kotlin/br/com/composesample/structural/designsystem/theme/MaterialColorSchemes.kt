package br.com.composesample.structural.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import br.com.composesample.structural.designsystem.tokens.Palette

internal val LightColorScheme =
    lightColorScheme(
        primary = Palette.Light.Primary,
        onPrimary = Palette.Light.OnPrimary,
        primaryContainer = Palette.Light.PrimaryContainer,
        onPrimaryContainer = Palette.Light.OnPrimaryContainer,
        secondary = Palette.Light.Secondary,
        onSecondary = Palette.Light.OnSecondary,
        secondaryContainer = Palette.Light.SecondaryContainer,
        onSecondaryContainer = Palette.Light.OnSecondaryContainer,
        background = Palette.Light.Background,
        surface = Palette.Light.Surface,
        onSurface = Palette.Light.OnSurface,
        surfaceVariant = Palette.Light.SurfaceVariant,
        onSurfaceVariant = Palette.Light.OnSurfaceVariant,
        outline = Palette.Light.Outline,
        outlineVariant = Palette.Light.OutlineVariant,
        onBackground = Palette.Light.OnSurface
    )

internal val DarkColorScheme =
    darkColorScheme(
        primary = Palette.Dark.Primary,
        onPrimary = Palette.Dark.OnPrimary,
        primaryContainer = Palette.Dark.PrimaryContainer,
        onPrimaryContainer = Palette.Dark.OnPrimaryContainer,
        secondary = Palette.Dark.Secondary,
        onSecondary = Palette.Dark.OnSecondary,
        secondaryContainer = Palette.Dark.SecondaryContainer,
        onSecondaryContainer = Palette.Dark.OnSecondaryContainer,
        background = Palette.Dark.Background,
        surface = Palette.Dark.Surface,
        onSurface = Palette.Dark.OnSurface,
        surfaceVariant = Palette.Dark.SurfaceVariant,
        onSurfaceVariant = Palette.Dark.OnSurfaceVariant,
        outline = Palette.Dark.Outline,
        outlineVariant = Palette.Dark.OutlineVariant,
        onBackground = Palette.Dark.OnSurface
    )
