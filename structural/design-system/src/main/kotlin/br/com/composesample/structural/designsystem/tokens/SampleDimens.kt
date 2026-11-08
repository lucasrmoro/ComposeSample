package br.com.composesample.structural.designsystem.tokens

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Reusable dimension tokens shared across Compose Sample components. */
internal object SampleDimens {

    object Size {
        val Size4 = 4.dp
        val Size8 = 8.dp
        val Size12 = 12.dp
        val Size16 = 16.dp
        val Size20 = 20.dp
        val Size24 = 24.dp
        val Size32 = 32.dp
        val Size40 = 40.dp
        val Size48 = 48.dp
        val Size64 = 64.dp
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
        val LabelSmall = 11.sp
        val LabelMedium = 12.sp
        val BodyLarge = 16.sp
        val LabelLineHeight = 16.sp
        val BodyLineHeight = 24.sp
        val LabelLetterSpacing = 0.5.sp
        val BodyLetterSpacing = 0.5.sp
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
