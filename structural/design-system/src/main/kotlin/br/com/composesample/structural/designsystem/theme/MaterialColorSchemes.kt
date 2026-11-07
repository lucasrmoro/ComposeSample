package br.com.composesample.structural.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import br.com.composesample.structural.designsystem.tokens.SampleColors

internal val LightColorScheme =
    lightColorScheme(
        primary = SampleColors.Light.Primary,
        onPrimary = SampleColors.Light.OnPrimary,
        primaryContainer = SampleColors.Light.PrimaryContainer,
        onPrimaryContainer = SampleColors.Light.OnPrimaryContainer,
        secondary = SampleColors.Light.Secondary,
        onSecondary = SampleColors.Light.OnSecondary,
        secondaryContainer = SampleColors.Light.SecondaryContainer,
        onSecondaryContainer = SampleColors.Light.OnSecondaryContainer,
        background = SampleColors.Light.Background,
        surface = SampleColors.Light.Surface,
        onSurface = SampleColors.Light.OnSurface,
        surfaceVariant = SampleColors.Light.SurfaceVariant,
        onSurfaceVariant = SampleColors.Light.OnSurfaceVariant,
        outline = SampleColors.Light.Outline,
        outlineVariant = SampleColors.Light.OutlineVariant,
        onBackground = SampleColors.Light.OnSurface
    )

internal val DarkColorScheme =
    darkColorScheme(
        primary = SampleColors.Dark.Primary,
        onPrimary = SampleColors.Dark.OnPrimary,
        primaryContainer = SampleColors.Dark.PrimaryContainer,
        onPrimaryContainer = SampleColors.Dark.OnPrimaryContainer,
        secondary = SampleColors.Dark.Secondary,
        onSecondary = SampleColors.Dark.OnSecondary,
        secondaryContainer = SampleColors.Dark.SecondaryContainer,
        onSecondaryContainer = SampleColors.Dark.OnSecondaryContainer,
        background = SampleColors.Dark.Background,
        surface = SampleColors.Dark.Surface,
        onSurface = SampleColors.Dark.OnSurface,
        surfaceVariant = SampleColors.Dark.SurfaceVariant,
        onSurfaceVariant = SampleColors.Dark.OnSurfaceVariant,
        outline = SampleColors.Dark.Outline,
        outlineVariant = SampleColors.Dark.OutlineVariant,
        onBackground = SampleColors.Dark.OnSurface
    )
