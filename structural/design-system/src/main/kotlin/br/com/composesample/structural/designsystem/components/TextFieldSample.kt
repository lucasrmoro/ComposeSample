package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
fun TextFieldSample(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    maxLength: Int = Int.MAX_VALUE,
    enabled: Boolean = true
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        enabled = enabled,
        onValueChange = { newValue ->
            if (newValue.length <= maxLength) onValueChange(newValue)
        },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CurrentTheme.color.primary,
            focusedLabelColor = CurrentTheme.color.primary,
            cursorColor = CurrentTheme.color.primary,
            focusedTextColor = CurrentTheme.color.onSurface,
            unfocusedTextColor = CurrentTheme.color.onSurface
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = false
    )
}

@Preview(showBackground = true)
@Composable
private fun SampleTextFieldPreview() {
    TextFieldSample(
        value = "",
        onValueChange = {},
        label = "Message",
        placeholder = "Type something here"
    )
}
