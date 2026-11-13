package br.com.composesample.structural.designsystem.components

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
fun SampleSegmentedButtonRow(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = selectedIndex == index,
                onClick = { onOptionSelected(index) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                icon = {}
            ) {
                SampleText(text = label)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleSegmentedButtonRowFirstSelectedPreview() {
    ComposeSampleTheme {
        SampleSegmentedButtonRow(
            modifier = Modifier.padding(CurrentTheme.spacing.medium),
            options = listOf("Movies", "Series"),
            selectedIndex = 0,
            onOptionSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleSegmentedButtonRowSecondSelectedPreview() {
    ComposeSampleTheme {
        SampleSegmentedButtonRow(
            modifier = Modifier.padding(CurrentTheme.spacing.medium),
            options = listOf("Movies", "Series"),
            selectedIndex = 1,
            onOptionSelected = {}
        )
    }
}
