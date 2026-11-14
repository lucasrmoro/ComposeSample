package br.com.composesample.structural.designsystem.tokens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

class IconToken internal constructor(internal val imageVector: ImageVector)

internal object SampleIcons {
    val Add = IconToken(Icons.Default.Add)
    val Star = IconToken(Icons.Default.Star)
    val Person = IconToken(Icons.Default.Person)
    val Search = IconToken(Icons.Default.Search)
    val Close = IconToken(Icons.Default.Close)
}
