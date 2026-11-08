package br.com.composesample.structural.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler

/** Opens a web destination only after an explicit user action. */
@Composable
fun SampleLink(modifier: Modifier = Modifier, url: String, label: String) {
    val handler = LocalUriHandler.current
    if (url.startsWith(HTTPS_SCHEME))
        SampleButton(
            modifier = modifier,
            text = label,
            onClick = { runCatching { handler.openUri(url) } },
            style = SampleButtonStyle.TEXT
        )
}

private const val HTTPS_SCHEME = "https://"
