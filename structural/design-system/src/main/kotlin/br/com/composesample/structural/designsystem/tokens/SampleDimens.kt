package br.com.composesample.structural.designsystem.tokens

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Reusable dimension tokens shared across Compose Sample components. */
internal object SampleDimens {

    object Size {
        val Small = 18.dp
        val Medium = 40.dp
        val FloatingActionClearance = 88.dp
        val TouchTarget = 48.dp
        val PosterWidth = 140.dp
        val PosterHeight = 200.dp
        val BackdropHeight = 200.dp
        val Portrait = 88.dp
        val RatingIcon = 14.dp
    }
    object Ratio {
        const val Video = 16f / 9f
    }
    object Spacing {
        val ExtraSmall = 4.dp
        val Small = 8.dp
        val Medium = 12.dp
        val Large = 16.dp
        val ExtraLarge = 24.dp
    }
    object Text {
        val SectionTitle = 20.sp
    }
    object Radius {
        val Small = 4.dp
        val Medium = 12.dp
    }
    object Stroke {
        val Thin = 2.dp
    }

}
