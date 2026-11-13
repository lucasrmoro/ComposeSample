package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme

/** Shows a themed dropdown menu anchored to the caller's content. */
@Composable
fun SampleDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest, content = content)
}

/** Adds a text action to a [SampleDropdownMenu]. */
@Composable
fun SampleDropdownMenuItem(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        modifier = modifier,
        text = { SampleText(text = text) },
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
private fun SampleDropdownMenuPreview() {
    ComposeSampleTheme {
        SampleDropdownMenu(expanded = true, onDismissRequest = {}) {
            SampleDropdownMenuItem(text = "Select item", onClick = {})
        }
    }
}
