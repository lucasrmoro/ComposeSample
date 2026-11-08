package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
fun SampleConnectionBanner() {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }
    ) {
        Box(
            Modifier.fillMaxWidth().padding(CurrentTheme.spacing.Small),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.sample_offline_notice),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
