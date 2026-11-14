package br.com.composesample.structural.designsystem.tokens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Opaque color token resolved using the current composition. */
class ColorToken internal constructor(internal val color: @Composable () -> Color)
