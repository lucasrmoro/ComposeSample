package br.com.composesample.structural.designsystem.tokens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

class SampleIcon internal constructor(internal val imageVector: ImageVector)

object SampleIcons {

    val Person = SampleIcon(Icons.Default.Person)

}
