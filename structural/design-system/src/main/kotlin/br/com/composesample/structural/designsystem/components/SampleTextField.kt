package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.structural.designsystem.tokens.IconToken

enum class SampleTextFieldStyle {
    FILLED,
    OUTLINED
}

enum class SampleKeyboardAction {
    DEFAULT,
    NONE,
    DONE,
    GO,
    NEXT,
    PREVIOUS,
    SEARCH,
    SEND
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
    TextFieldContent(
        modifier = modifier,
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= maxLength) onValueChange(newValue)
        },
        enabled = enabled,
        singleLine = singleLine,
        style = style,
        keyboardOptions = keyboardAction.toKeyboardOptions(),
        keyboardActions = keyboardAction.toKeyboardActions(onKeyboardAction),
        label = textFieldText(label),
        placeholder = textFieldText(placeholder),
        leadingIcon = textFieldIcon(leadingIcon, leadingIconDescription),
        trailingIcon = textFieldIcon(trailingIcon, trailingIconDescription, onTrailingIconClick)
    )
}

@Composable
private fun TextFieldContent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    singleLine: Boolean,
    style: SampleTextFieldStyle,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    label: (@Composable () -> Unit)?,
    placeholder: (@Composable () -> Unit)?,
    leadingIcon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?
) {
    when (style) {
        SampleTextFieldStyle.FILLED -> TextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            enabled = enabled,
            onValueChange = onValueChange,
            singleLine = singleLine,
            shape = if (singleLine) CircleShape else TextFieldDefaults.shape,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
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
            onValueChange = onValueChange,
            singleLine = singleLine,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
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

private fun textFieldText(text: String?): (@Composable () -> Unit)? = text?.let { value ->
    { SampleText(text = value) }
}

private fun textFieldIcon(
    icon: IconToken?,
    contentDescription: String?,
    onClick: (() -> Unit)? = null
): (@Composable () -> Unit)? = icon?.let { iconToken ->
    {
        if (onClick == null) {
            TextFieldIcon(icon = iconToken, contentDescription = contentDescription)
        } else {
            IconButton(onClick = onClick) {
                TextFieldIcon(icon = iconToken, contentDescription = contentDescription)
            }
        }
    }
}

@Composable
private fun TextFieldIcon(
    modifier: Modifier = Modifier,
    icon: IconToken,
    contentDescription: String?
) {
    SampleIcon(
        modifier = modifier,
        icon = icon,
        contentDescription = contentDescription,
        tint = CurrentTheme.colors.onSurfaceVariant
    )
}

private fun SampleKeyboardAction.toKeyboardOptions(): KeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Text,
    imeAction = toImeAction()
)

private fun SampleKeyboardAction.toImeAction(): ImeAction = when (this) {
    SampleKeyboardAction.DEFAULT -> ImeAction.Default
    SampleKeyboardAction.NONE -> ImeAction.None
    SampleKeyboardAction.DONE -> ImeAction.Done
    SampleKeyboardAction.GO -> ImeAction.Go
    SampleKeyboardAction.NEXT -> ImeAction.Next
    SampleKeyboardAction.PREVIOUS -> ImeAction.Previous
    SampleKeyboardAction.SEARCH -> ImeAction.Search
    SampleKeyboardAction.SEND -> ImeAction.Send
}

private fun SampleKeyboardAction.toKeyboardActions(onAction: () -> Unit): KeyboardActions {
    val action: KeyboardActionScope.() -> Unit = { onAction() }
    return when (this) {
        SampleKeyboardAction.DEFAULT, SampleKeyboardAction.NONE -> KeyboardActions.Default
        SampleKeyboardAction.DONE -> KeyboardActions(onDone = action)
        SampleKeyboardAction.GO -> KeyboardActions(onGo = action)
        SampleKeyboardAction.NEXT -> KeyboardActions(onNext = action)
        SampleKeyboardAction.PREVIOUS -> KeyboardActions(onPrevious = action)
        SampleKeyboardAction.SEARCH -> KeyboardActions(onSearch = action)
        SampleKeyboardAction.SEND -> KeyboardActions(onSend = action)
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
        modifier = modifier.padding(CurrentTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small)
    ) {
        SampleTextField(
            value = "Preview message",
            onValueChange = {},
            label = "Message",
            placeholder = "Type something here",
            style = style
        )
        SampleTextField(
            value = "Preview message",
            onValueChange = {},
            label = "Message",
            placeholder = "Type something here",
            enabled = false,
            style = style
        )
    }
}
