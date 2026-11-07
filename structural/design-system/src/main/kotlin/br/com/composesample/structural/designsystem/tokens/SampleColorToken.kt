package br.com.composesample.structural.designsystem.tokens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class SampleColorToken internal constructor(internal val color: @Composable () -> Color)

object SampleColorTokens {

    val OnSurfaceVariant = SampleColorToken { MaterialTheme.colorScheme.onSurfaceVariant }

}
