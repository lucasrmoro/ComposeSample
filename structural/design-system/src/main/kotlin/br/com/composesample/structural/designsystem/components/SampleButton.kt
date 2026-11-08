package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

enum class SampleButtonStyle {
    FILLED,
    OUTLINED,
    TEXT
}

@Composable
fun SampleButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    style: SampleButtonStyle = SampleButtonStyle.FILLED,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val contentColor = if (style == SampleButtonStyle.FILLED) {
        CurrentTheme.color.onPrimary
    } else {
        CurrentTheme.color.primary
    }
    val buttonContent: @Composable RowScope.() -> Unit = {
        if (isLoading) {
            SampleCircularProgressIndicator(
                modifier = Modifier.size(CurrentTheme.size.Small),
                color = contentColor
            )
            Spacer(modifier = Modifier.width(CurrentTheme.spacing.Small))
        }
        SampleText(text = text)
    }
    when (style) {
        SampleButtonStyle.FILLED -> Button(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = CurrentTheme.color.primary,
                contentColor = contentColor
            ),
            content = buttonContent
        )
        SampleButtonStyle.OUTLINED -> OutlinedButton(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled && !isLoading,
            content = buttonContent
        )
        SampleButtonStyle.TEXT -> TextButton(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled && !isLoading,
            content = buttonContent
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleButtonPreview() {
    ComposeSampleTheme {
        SampleButton(text = "Continue", onClick = {})
    }
}
