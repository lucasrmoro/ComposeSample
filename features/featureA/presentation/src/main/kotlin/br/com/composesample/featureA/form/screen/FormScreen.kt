package br.com.composesample.featureA.form.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.featureA.form.model.FormUiEffect
import br.com.composesample.featureA.form.model.FormUiEvent
import br.com.composesample.featureA.form.model.FormUiState
import br.com.composesample.featureA.form.viewModel.FormViewModel
import br.com.composesample.structural.designsystem.components.SampleButton
import br.com.composesample.structural.designsystem.components.SampleScreen
import br.com.composesample.structural.designsystem.components.SampleTextField
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.structural.designsystem.screen.BaseScreen

internal class FormScreen(
    private val viewModel: FormViewModel,
    private val onNavigateBack: () -> Unit
) : BaseScreen<FormUiState, FormUiEffect>(viewModel) {

    @Composable
    override fun Content(uiState: FormUiState) {
        FormScreenContent(
            state = uiState,
            sendEvent = viewModel::sendEvent,
            onNavigateBack = onNavigateBack
        )
    }

    override fun consumeEffect(effect: FormUiEffect) {
        when (effect) {
            FormUiEffect.NavigateBack -> onNavigateBack()
        }
    }
}

@Composable
private fun FormScreenContent(
    state: FormUiState,
    sendEvent: (FormUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    SampleScreen(
        title = "Compose - Sample",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = CurrentTheme.spacing.ExtraLarge),
            verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.Large)
        ) {
            SampleTextField(
                value = state.message,
                onValueChange = { sendEvent(FormUiEvent.MessageChanged(it)) },
                label = "Message",
                placeholder = "Type something here"
            )
            SampleButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Save",
                onClick = { sendEvent(FormUiEvent.SaveClicked) },
                isLoading = state.isSaving
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormScreenPreview() {
    ComposeSampleTheme {
        FormScreenContent(
            state = FormUiState(message = "Preview message"),
            sendEvent = {},
            onNavigateBack = {}
        )
    }
}
