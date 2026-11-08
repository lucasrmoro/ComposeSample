package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler

/** Opens a web destination only after an explicit user action. */
@Composable
fun SampleLink(url: String, label: String) {
    val handler = LocalUriHandler.current
    if (url.startsWith(HTTPS_SCHEME))
        TextButton(onClick = { runCatching { handler.openUri(url) } }) { SampleText(text = label) }
}

private const val HTTPS_SCHEME = "https://"
