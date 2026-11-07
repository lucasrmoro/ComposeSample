package br.com.composesample.structural.designsystem.tokens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class SampleColor internal constructor(internal val color: @Composable () -> Color)
