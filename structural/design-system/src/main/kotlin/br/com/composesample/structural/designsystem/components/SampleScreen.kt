package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

enum class LoadingState {
    ONLY_LOADING,
    LOADING_DIM_BG,
    NONE
}

@Composable
fun SampleScreen(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    loadingState: LoadingState = LoadingState.NONE,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        containerColor = CurrentTheme.color.background,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.ime),
        topBar = {
            SampleHeader(
                title = title,
                canNavigateBack = canNavigateBack,
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = floatingActionButton,
        content = { paddingValues ->
            val layoutDirection = LocalLayoutDirection.current
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    top = paddingValues.calculateTopPadding(),
                    end = paddingValues.calculateEndPadding(layoutDirection)
                )
                .navigationBarsPadding()
                .imePadding()
            when (loadingState) {
                LoadingState.ONLY_LOADING -> {
                    Box(
                        modifier = contentModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        SampleCircularProgressIndicator(
                            modifier = Modifier.size(CurrentTheme.size.Medium),
                            color = CurrentTheme.color.primary
                        )
                    }
                }

                LoadingState.LOADING_DIM_BG -> {
                    Box(modifier = contentModifier) {
                        content()
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CurrentTheme.color.scrim),
                            contentAlignment = Alignment.Center
                        ) {
                            SampleCircularProgressIndicator(
                                modifier = Modifier.size(CurrentTheme.size.Medium),
                                color = CurrentTheme.color.primary
                            )
                        }
                    }
                }

                LoadingState.NONE -> Box(modifier = contentModifier) { content() }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SampleScreenPreview() {
    ComposeSampleTheme {
        SampleScreen(
            title = "Compose - Sample",
            canNavigateBack = true,
            onNavigateBack = {}
        ) { }
    }
}
