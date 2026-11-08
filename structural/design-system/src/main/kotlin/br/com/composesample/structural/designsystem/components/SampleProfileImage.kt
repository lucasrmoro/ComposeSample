package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.com.composesample.structural.assets_cache.AssetRequest
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
fun SampleProfileImage(
    modifier: Modifier = Modifier,
    request: AssetRequest?,
    contentDescription: String
) {
    if (request == null) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            SampleIcon(
                icon = CurrentTheme.icon.person,
                contentDescription = stringResource(R.string.sample_photo_not_provided),
                tint = CurrentTheme.colors.onSurfaceVariant
            )
        }
        return
    }
    SampleImage(request = request, contentDescription = contentDescription, modifier = modifier)
}
