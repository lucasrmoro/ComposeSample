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
        leadingIcon = CurrentTheme.icon.search,
        leadingIconDescription = stringResource(R.string.sample_search),
        trailingIcon = CurrentTheme.icon.close.takeIf { value.isNotEmpty() },
        trailingIconDescription = if (value.isNotEmpty()) stringResource(R.string.sample_clear_search) else null,
        onTrailingIconClick = { onValueChange("") }
    )
}

@Preview(showBackground = true)
@Composable
private fun SampleSearchBarPreview() {
    ComposeSampleTheme {
        Column(
            modifier = Modifier.padding(CurrentTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small)
        ) {
            SampleSearchBar(
                value = "",
                placeholder = "Search movies",
                onValueChange = {},
                onSearch = {}
            )
            SampleSearchBar(
                value = "Dune",
                placeholder = "Search movies",
                onValueChange = {},
                onSearch = {}
            )
        }
    }
}
