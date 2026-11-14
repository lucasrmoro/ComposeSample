package br.com.composesample.structural.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleHeader(
    modifier: Modifier = Modifier,
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = { SampleText(text = title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CurrentTheme.color.surface,
            titleContentColor = CurrentTheme.color.onSurface,
            navigationIconContentColor = CurrentTheme.color.primary
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.sample_navigate_back)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HeaderPreview() {
    ComposeSampleTheme {
        SampleHeader(title = "Compose - Sample", canNavigateBack = true, onNavigateBack = {})
    }
}
