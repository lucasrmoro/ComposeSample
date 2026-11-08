package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
fun SampleSearchBar(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    SampleTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        singleLine = true,
        style = SampleTextFieldStyle.FILLED,
        keyboardAction = SampleKeyboardAction.SEARCH,
        onKeyboardAction = {
            onSearch()
            focusManager.clearFocus()
        },
        leadingIcon = CurrentTheme.icon.Search,
        leadingIconDescription = stringResource(R.string.sample_search),
        trailingIcon = CurrentTheme.icon.Close.takeIf { value.isNotEmpty() },
        trailingIconDescription = if (value.isNotEmpty()) {
            stringResource(R.string.sample_clear_search)
        } else {
            null
        },
        onTrailingIconClick = { onValueChange("") }
    )
}

@Preview(showBackground = true)
@Composable
private fun SampleSearchBarPreview() {
    ComposeSampleTheme {
        Column(
            modifier = Modifier.padding(CurrentTheme.spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.Small)
        ) {
            SampleSearchBar(
                value = "",
                placeholder = stringResource(R.string.sample_search_preview_placeholder),
                onValueChange = {},
                onSearch = {}
            )
            SampleSearchBar(
                value = stringResource(R.string.sample_search_preview_value),
                placeholder = stringResource(R.string.sample_search_preview_placeholder),
                onValueChange = {},
                onSearch = {}
            )
        }
    }
}
