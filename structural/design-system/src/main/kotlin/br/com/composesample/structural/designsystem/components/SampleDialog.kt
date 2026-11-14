package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
fun SampleDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        containerColor = CurrentTheme.color.surface,
        titleContentColor = CurrentTheme.color.onSurface,
        textContentColor = CurrentTheme.color.onSurface,
        title = { SampleText(text = title) },
        text = { SampleText(text = message) },
        confirmButton = {
            SampleButton(
                text = confirmText,
                onClick = onConfirm,
                style = SampleButtonStyle.TEXT
            )
        },
        dismissButton = {
            SampleButton(
                text = dismissText,
                onClick = onDismiss,
                style = SampleButtonStyle.TEXT
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SampleDialogPreview() {
    ComposeSampleTheme {
        SampleDialog(
            title = "Discard changes?",
            message = "Your changes will be lost.",
            confirmText = "Discard",
            dismissText = "Cancel",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
