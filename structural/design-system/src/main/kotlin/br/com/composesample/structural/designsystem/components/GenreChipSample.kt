package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.tokens.Dimen

@Composable
fun GenreChipSample(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.height(Dimen.Size.TouchTarget),
        contentAlignment = Alignment.CenterStart
    ) {
        AssistChip(
            onClick = onClick,
            label = { Text(label) },
            leadingIcon = {
                Icon(painterResource(R.drawable.sample_filter_list), contentDescription = null)
            }
        )
    }
}
