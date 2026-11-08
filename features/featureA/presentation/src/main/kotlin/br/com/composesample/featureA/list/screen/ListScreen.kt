package br.com.composesample.featureA.list.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.featureA.model.SomeModel
import br.com.composesample.featureA.list.model.ListUiEffect
import br.com.composesample.featureA.list.model.ListUiEvent
import br.com.composesample.featureA.list.model.ListUiState
import br.com.composesample.featureA.list.viewModel.ListViewModel
import br.com.composesample.structural.designsystem.components.LoadingState
import br.com.composesample.structural.designsystem.components.SampleScreen
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.structural.designsystem.screen.BaseScreen

internal class ListScreen(
    private val viewModel: ListViewModel,
    private val onOpenForm: () -> Unit
) : BaseScreen<ListUiState, ListUiEffect>(viewModel) {

    @Composable
    override fun Content(uiState: ListUiState) {
        LaunchedEffect(Unit) {
            viewModel.sendEvent(ListUiEvent.LoadItems)
        }
        ListScreenContent(
            state = uiState,
            sendEvent = viewModel::sendEvent
        )
    }

    override fun consumeEffect(effect: ListUiEffect) {
        when (effect) {
            ListUiEffect.NavigateToForm -> onOpenForm()
        }
    }
}

@Composable
private fun ListScreenContent(
    state: ListUiState,
    sendEvent: (ListUiEvent) -> Unit
) {
    SampleScreen(
        title = "Compose - Sample",
        canNavigateBack = false,
        onNavigateBack = {},
        loadingState = if (state.isLoading) LoadingState.ONLY_LOADING else LoadingState.NONE,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { sendEvent(ListUiEvent.AddItemClicked) },
                containerColor = CurrentTheme.color.secondary,
                contentColor = CurrentTheme.color.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = CurrentTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.medium)
        ) {
            items(state.items, key = { it.id }) { item ->
                Text(text = item.text)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListScreenPreview() {
    ComposeSampleTheme {
        ListScreenContent(
            state = ListUiState(
                items = listOf(
                    SomeModel(id = 1, text = "First item"),
                    SomeModel(id = 2, text = "Second item")
                )
            ),
            sendEvent = {}
        )
    }
}
