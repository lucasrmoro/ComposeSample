package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme

/** Draws a themed horizontal divider between content groups. */
@Composable
fun SampleDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun SampleDividerPreview() {
    ComposeSampleTheme { SampleDivider() }
}
