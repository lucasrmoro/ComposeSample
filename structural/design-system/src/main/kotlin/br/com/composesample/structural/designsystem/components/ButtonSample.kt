package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
fun ButtonSample(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = CurrentTheme.color.primary,
            contentColor = CurrentTheme.color.onPrimary
        )
    ) {
        if (isLoading) {
            CircularProgressIndicatorSample(
                modifier = Modifier.size(CurrentTheme.size.Small),
                color = CurrentTheme.color.onPrimary
            )
            Spacer(modifier = Modifier.width(CurrentTheme.spacing.Small))
        }
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleButtonPreview() {
    ComposeSampleTheme {
        ButtonSample(text = "Continue", onClick = {})
    }
}
