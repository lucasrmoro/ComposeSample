package br.com.composesample.structural.designsystem.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.composesample.structural.core.text.UiText

/** Resolves resource text and positional arguments against the current Compose configuration. */
@Composable
fun UiText.asString(): String = when (this) {
    UiText.None -> ""
    is UiText.Raw -> value
    is UiText.StringResource -> stringResource(id, *params.toTypedArray())
}
