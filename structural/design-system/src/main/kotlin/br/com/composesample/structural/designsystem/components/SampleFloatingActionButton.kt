package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.structural.designsystem.tokens.IconToken

@Composable
fun SampleFloatingActionButton(
    modifier: Modifier = Modifier,
    icon: IconToken,
    contentDescription: String,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = CurrentTheme.color.secondary,
        contentColor = CurrentTheme.color.onSecondary
    ) {
        SampleIcon(
            icon = icon,
            contentDescription = contentDescription,
            tint = CurrentTheme.colors.onSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleFloatingActionButtonPreview() {
    ComposeSampleTheme {
        SampleFloatingActionButton(
            icon = CurrentTheme.icon.add,
            contentDescription = "Add item",
            onClick = {}
        )
    }
}
