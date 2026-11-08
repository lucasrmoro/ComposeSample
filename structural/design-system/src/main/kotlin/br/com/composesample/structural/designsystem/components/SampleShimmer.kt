package br.com.composesample.structural.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
internal fun SampleShimmer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "SampleShimmer")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_DURATION_MILLIS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Shimmer progress"
    )
    val baseColor = CurrentTheme.colors.shimmer.color()
    val highlightColor = CurrentTheme.color.surface
    Box(
        modifier = modifier.progressSemantics().drawWithCache {
            val colors = listOf(baseColor, highlightColor, baseColor)
            val distance = size.width + size.height
            val bandWidth = distance / 2f
            onDrawBehind {
                if (size.width > 0f && size.height > 0f) {
                    val offset = (distance + bandWidth) * progress.value - bandWidth
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = colors,
                            start = Offset(offset / 2f, offset / 2f),
                            end = Offset((offset + bandWidth) / 2f, (offset + bandWidth) / 2f)
                        )
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SampleShimmerPreview() {
    ComposeSampleTheme {
        SampleShimmer(modifier = Modifier.size(CurrentTheme.size.huge))
    }
}

private const val SHIMMER_DURATION_MILLIS = 1200
