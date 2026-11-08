package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
fun SampleCircularProgressIndicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = CurrentTheme.stroke.thin
    )
}

@Preview(showBackground = true)
@Composable
private fun SampleCircularProgressIndicatorPreview() {
    ComposeSampleTheme {
        SampleCircularProgressIndicator(
            modifier = Modifier.size(CurrentTheme.size.size40),
            color = CurrentTheme.color.primary
        )
    }
}
