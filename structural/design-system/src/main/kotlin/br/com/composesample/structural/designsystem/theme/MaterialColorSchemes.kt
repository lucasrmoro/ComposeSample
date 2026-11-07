package br.com.composesample.structural.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import br.com.composesample.structural.designsystem.tokens.SamplePalette

internal val LightColorScheme =
    lightColorScheme(
        primary = SamplePalette.Purple.S40,
        onPrimary = SamplePalette.Neutral.White,
        primaryContainer = SamplePalette.Purple.S10,
        onPrimaryContainer = SamplePalette.Purple.S100,
        secondary = SamplePalette.VioletGray.S40,
        onSecondary = SamplePalette.Neutral.White,
        secondaryContainer = SamplePalette.VioletGray.S10,
        onSecondaryContainer = SamplePalette.VioletGray.S100,
        background = SamplePalette.Surfaces.WarmWhite,
        surface = SamplePalette.Surfaces.WarmWhite,
        onSurface = SamplePalette.Content.Ink,
        surfaceVariant = SamplePalette.Surfaces.Lavender,
        onSurfaceVariant = SamplePalette.Content.Muted,
        outline = SamplePalette.Outlines.Muted,
        outlineVariant = SamplePalette.Outlines.Lavender,
        onBackground = SamplePalette.Content.Ink
    )

internal val DarkColorScheme =
    darkColorScheme(
        primary = SamplePalette.Purple.S80,
        onPrimary = SamplePalette.Purple.S90,
        primaryContainer = SamplePalette.Purple.S20,
        onPrimaryContainer = SamplePalette.Purple.S10,
        secondary = SamplePalette.VioletGray.S80,
        onSecondary = SamplePalette.VioletGray.S90,
        secondaryContainer = SamplePalette.VioletGray.S20,
        onSecondaryContainer = SamplePalette.VioletGray.S10,
        background = SamplePalette.Surfaces.Night,
        surface = SamplePalette.Surfaces.Night,
        onSurface = SamplePalette.Content.Snow,
        surfaceVariant = SamplePalette.Surfaces.Charcoal,
        onSurfaceVariant = SamplePalette.Content.SoftLavender,
        outline = SamplePalette.Outlines.Soft,
        outlineVariant = SamplePalette.Outlines.Plum,
        onBackground = SamplePalette.Content.Snow
    )
