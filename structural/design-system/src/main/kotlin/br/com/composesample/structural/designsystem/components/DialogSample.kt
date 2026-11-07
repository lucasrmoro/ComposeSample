package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
fun DialogSample(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CurrentTheme.color.surface,
        titleContentColor = CurrentTheme.color.onSurface,
        textContentColor = CurrentTheme.color.onSurface,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = CurrentTheme.color.primary
                )
            ) { Text(confirmText) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = CurrentTheme.color.primary
                )
            ) { Text(dismissText) }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SampleDialogPreview() {
    ComposeSampleTheme {
        DialogSample(
            title = "Discard changes?",
            message = "Your changes will be lost.",
            confirmText = "Discard",
            dismissText = "Cancel",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
