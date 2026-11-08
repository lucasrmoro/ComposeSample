package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.structural.designsystem.tokens.IconToken

enum class SampleTextFieldStyle {
    FILLED,
    OUTLINED
}

enum class SampleKeyboardAction {
    DEFAULT,
    SEARCH
}

@Composable
fun SampleTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String? = null,
    maxLength: Int = Int.MAX_VALUE,
    enabled: Boolean = true,
    singleLine: Boolean = false,
    style: SampleTextFieldStyle = SampleTextFieldStyle.OUTLINED,
    keyboardAction: SampleKeyboardAction = SampleKeyboardAction.DEFAULT,
    onKeyboardAction: () -> Unit = {},
    leadingIcon: IconToken? = null,
    leadingIconDescription: String? = null,
    trailingIcon: IconToken? = null,
    trailingIconDescription: String? = null,
    onTrailingIconClick: () -> Unit = {}
) {
    val imeAction = when (keyboardAction) {
        SampleKeyboardAction.DEFAULT -> ImeAction.Default
        SampleKeyboardAction.SEARCH -> ImeAction.Search
    }
    val keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = imeAction
    )
    val keyboardActions = KeyboardActions(
        onSearch = if (keyboardAction == SampleKeyboardAction.SEARCH) {
            { onKeyboardAction() }
        } else {
            null
        }
    )
    val onValueChangeWithinLimit: (String) -> Unit = { newValue ->
        if (newValue.length <= maxLength) onValueChange(newValue)
    }
    val labelContent: (@Composable () -> Unit)? = label?.let { text ->
        { SampleText(text = text) }
    }
    val leadingIconContent: (@Composable () -> Unit)? = leadingIcon?.let { icon ->
        {
            SampleIcon(
                icon = icon,
                contentDescription = leadingIconDescription,
                tint = CurrentTheme.colors.OnSurfaceVariant
            )
        }
    }
    val trailingIconContent: (@Composable () -> Unit)? = trailingIcon?.let { icon ->
        {
            IconButton(onClick = onTrailingIconClick) {
                SampleIcon(
                    icon = icon,
                    contentDescription = trailingIconDescription,
                    tint = CurrentTheme.colors.OnSurfaceVariant
                )
            }
        }
    }
    val placeholderContent: @Composable () -> Unit = { SampleText(text = placeholder) }

    when (style) {
        SampleTextFieldStyle.FILLED -> TextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            enabled = enabled,
            onValueChange = onValueChangeWithinLimit,
            singleLine = singleLine,
            shape = if (singleLine) CircleShape else TextFieldDefaults.shape,
            label = labelContent,
            placeholder = placeholderContent,
            leadingIcon = leadingIconContent,
            trailingIcon = trailingIconContent,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                cursorColor = CurrentTheme.color.primary,
                focusedTextColor = CurrentTheme.color.onSurface,
                unfocusedTextColor = CurrentTheme.color.onSurface
            )
        )
        SampleTextFieldStyle.OUTLINED -> OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            enabled = enabled,
            onValueChange = onValueChangeWithinLimit,
            singleLine = singleLine,
            label = labelContent,
            placeholder = placeholderContent,
            leadingIcon = leadingIconContent,
            trailingIcon = trailingIconContent,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CurrentTheme.color.primary,
                focusedLabelColor = CurrentTheme.color.primary,
                cursorColor = CurrentTheme.color.primary,
                focusedTextColor = CurrentTheme.color.onSurface,
                unfocusedTextColor = CurrentTheme.color.onSurface
            )
        )
    }
}

@Preview(name = "Filled", showBackground = true)
@Composable
private fun SampleFilledTextFieldPreview() {
    ComposeSampleTheme {
        SampleTextFieldStylePreview(style = SampleTextFieldStyle.FILLED)
    }
}

@Preview(name = "Outlined", showBackground = true)
@Composable
private fun SampleOutlinedTextFieldPreview() {
    ComposeSampleTheme {
        SampleTextFieldStylePreview(style = SampleTextFieldStyle.OUTLINED)
    }
}

@Composable
private fun SampleTextFieldStylePreview(
    modifier: Modifier = Modifier,
    style: SampleTextFieldStyle
) {
    Column(
        modifier = modifier.padding(CurrentTheme.spacing.Medium),
        verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.Small)
    ) {
        SampleTextField(
            value = stringResource(R.string.sample_text_field_preview_value),
            onValueChange = {},
            label = stringResource(R.string.sample_text_field_preview_label),
            placeholder = stringResource(R.string.sample_text_field_preview_placeholder),
            style = style
        )
        SampleTextField(
            value = stringResource(R.string.sample_text_field_preview_value),
            onValueChange = {},
            label = stringResource(R.string.sample_text_field_preview_label),
            placeholder = stringResource(R.string.sample_text_field_preview_placeholder),
            enabled = false,
            style = style
        )
    }
}
