package br.com.composesample.structural.designsystem.tokens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

class IconToken internal constructor(internal val imageVector: ImageVector)

internal object SampleIcons {

    val Person = IconToken(Icons.Default.Person)

}
