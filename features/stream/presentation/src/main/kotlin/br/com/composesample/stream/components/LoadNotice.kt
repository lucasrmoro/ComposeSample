package br.com.composesample.stream.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.structural.core.text.UiText
import br.com.composesample.structural.designsystem.components.SampleButton
import br.com.composesample.structural.designsystem.components.SampleButtonStyle
import br.com.composesample.structural.designsystem.components.SampleCircularProgressIndicator
import br.com.composesample.structural.designsystem.components.SampleText
import br.com.composesample.structural.designsystem.text.asString
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
internal fun LoadNotice(loading: Boolean, empty: Boolean, error: UiText?, retry: () -> Unit) {
    if (loading && empty) SampleCircularProgressIndicator()
    error?.let { errorText ->
        SampleText(text = errorText.asString(), style = CurrentTheme.typography.bodySmall)
        SampleButton(
            text = stringResource(R.string.stream_retry),
            onClick = retry,
            style = SampleButtonStyle.TEXT
        )
    } ?: run {
        if (empty && !loading) SampleText(text = stringResource(R.string.stream_empty))
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadNoticePreview() {
    ComposeSampleTheme {
        Column {
            LoadNotice(loading = true, empty = true, error = null, retry = {})
            LoadNotice(loading = false, empty = true, error = null, retry = {})
            LoadNotice(
                loading = false,
                empty = true,
                error = UiText.Raw("Could not load"),
                retry = {}
            )
        }
    }
}
