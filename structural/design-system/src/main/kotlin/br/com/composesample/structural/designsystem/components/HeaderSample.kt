package br.com.composesample.structural.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSample(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
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
                        contentDescription = "Navigate back"
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
        HeaderSample(title = "Compose - Sample", canNavigateBack = true, onNavigateBack = {})
    }
}
