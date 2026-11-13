package br.com.composesample.stream.details.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.components.ImageRole
import br.com.composesample.stream.components.LoadNotice
import br.com.composesample.stream.components.RATING_FORMAT
import br.com.composesample.stream.components.imageRequest
import br.com.composesample.stream.details.model.DetailsUiEffect
import br.com.composesample.stream.details.model.DetailsUiEvent
import br.com.composesample.stream.details.model.DetailsUiState
import br.com.composesample.stream.details.viewModel.DetailsViewModel
import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.providers.ProviderCategory
import br.com.composesample.stream.model.providers.WatchProvider
import br.com.composesample.stream.tokens.StreamDimens
import br.com.composesample.structural.designsystem.components.SampleButton
import br.com.composesample.structural.designsystem.components.SampleButtonStyle
import br.com.composesample.structural.designsystem.components.SampleCircularProgressIndicator
import br.com.composesample.structural.designsystem.components.SampleDivider
import br.com.composesample.structural.designsystem.components.SampleDropdownMenu
import br.com.composesample.structural.designsystem.components.SampleDropdownMenuItem
import br.com.composesample.structural.designsystem.components.SampleImage
import br.com.composesample.structural.designsystem.components.SampleLink
import br.com.composesample.structural.designsystem.components.SampleProfileImage
import br.com.composesample.structural.designsystem.components.SampleScreen
import br.com.composesample.structural.designsystem.components.SampleText
import br.com.composesample.structural.designsystem.components.SampleVideoPlayer
import br.com.composesample.structural.designsystem.screen.BaseScreen
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.structural.video_player.VideoSource

internal class DetailsScreen(
    private val detailsViewModel: DetailsViewModel,
    private val id: MediaId,
    private val back: () -> Unit
) : BaseScreen<DetailsUiState, DetailsUiEffect>(detailsViewModel) {

    @Composable
    override fun Content(uiState: DetailsUiState) {
        LaunchedEffect(id) { detailsViewModel.sendEvent(DetailsUiEvent.Start(id)) }
        DetailsContent(uiState, back, detailsViewModel::sendEvent)
    }

    override fun consumeEffect(effect: DetailsUiEffect) = Unit

}

@Composable
private fun Info(label: Int, value: String?) {
    if (!value.isNullOrBlank()) Column(Modifier.padding(vertical = CurrentTheme.spacing.xSmall)) {
            SampleText(
                    text = stringResource(label),
                style = CurrentTheme.typography.labelMedium,
                color = CurrentTheme.color.onSurface
            )
            SampleText(text = value, style = CurrentTheme.typography.bodyMedium)
        }
}

@Composable
private fun DetailsContent(
    state: DetailsUiState,
    back: () -> Unit,
    event: (DetailsUiEvent) -> Unit
) {
    SampleScreen(
        title = stringResource(R.string.stream_details),
        canNavigateBack = true,
        onNavigateBack = back
    ) {
        val mediaDetails = state.details
        LazyColumn(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small)
        ) {
            item {
                Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                    LoadNotice(
                        state.loading,
                        mediaDetails == null,
                        state.error,
                        { event(DetailsUiEvent.Retry) }
                    )
                }
            }
            mediaDetails?.let { details ->
                item {
                    SampleImage(
                        request = imageRequest(details.backdropPath, ImageRole.BACKDROP),
                        contentDescription = details.summary.title,
                        modifier = Modifier.fillMaxWidth().height(StreamDimens.backdropHeight)
                    )
                }
                item {
                    Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                        SampleText(text = details.summary.title, style = CurrentTheme.typography.headlineSmall)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small)
                        ) {
                            details.summary.rating?.let {
                                SampleText(
                                        text = String.format(java.util.Locale.getDefault(), RATING_FORMAT, it),
                                    style = CurrentTheme.typography.labelMedium
                                )
                            }
                            details.summary.year?.let {
                                SampleText(text = it, style = CurrentTheme.typography.labelMedium)
                            }
                            details.summary.certification?.let {
                                SampleText(text = it, style = CurrentTheme.typography.labelMedium)
                            }
                        }
                        SampleText(
                                text = details.genres.joinToString(" · ", transform = Genre::name),
                            style = CurrentTheme.typography.bodySmall
                        )
                        Info(
                            R.string.stream_runtime,
                            details.runtimeMinutes?.let {
                                stringResource(R.string.stream_minutes, it)
                            }
                        )
                        Info(R.string.stream_release_date, details.releaseDate)
                        Info(R.string.stream_original_title, details.originalTitle)
                    }
                }
                details.overview?.let { overview ->
                    item {
                        Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                            SampleText(
                                    text = stringResource(R.string.stream_synopsis),
                                style = CurrentTheme.typography.titleLarge
                            )
                            SampleText(text = overview)
                        }
                    }
                }
                val trailer =
                    details.videos.firstOrNull {
                        it.site.equals("YouTube", true) && it.type == "Trailer"
                    } ?: details.videos.firstOrNull { it.site.equals("YouTube", true) }
                trailer?.let { trailer ->
                    item {
                        Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                            SampleText(
                                    text = stringResource(R.string.stream_trailer),
                                style = CurrentTheme.typography.titleLarge
                            )
                            SampleVideoPlayer(
                                source = VideoSource(provider = trailer.site, key = trailer.key),
                                online = state.online
                            )
                            SampleText(text = trailer.name, style = CurrentTheme.typography.bodySmall)
                        }
                    }
                }
                if (details.providers.isNotEmpty())
                    item {
                        Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                            SampleText(
                                    text = stringResource(R.string.stream_watch),
                                style = CurrentTheme.typography.titleLarge
                            )
                            details.providers
                                .groupBy(WatchProvider::category)
                                .forEach { (category, providers) ->
                                    SampleText(
                                            text = stringResource(
                                            when (category) {
                                                ProviderCategory.SUBSCRIPTION ->
                                                    R.string.stream_subscription
                                                ProviderCategory.FREE -> R.string.stream_free
                                                ProviderCategory.ADS -> R.string.stream_ads
                                                ProviderCategory.RENT -> R.string.stream_rent
                                                ProviderCategory.BUY -> R.string.stream_buy
                                            }
                                        ),
                                        style = CurrentTheme.typography.titleSmall
                                    )
                                    providers.forEach { provider ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(
                                                CurrentTheme.spacing.small
                                            )
                                        ) {
                                            SampleImage(
                                                request = imageRequest(
                                                    path = provider.logoPath,
                                                    role = ImageRole.LOGO
                                                ),
                                                contentDescription = provider.name,
                                                modifier = Modifier.size(CurrentTheme.size.xxxLarge)
                                            )
                                            SampleText(text = provider.name)
                                        }
                                    }
                                }
                            SampleText(
                                    text = stringResource(R.string.stream_justwatch),
                                style = CurrentTheme.typography.labelSmall
                            )
                            details.providerLink?.let {
                                SampleLink(url = it, label = stringResource(R.string.stream_watch))
                            }
                        }
                    }
                if (details.seasons.isNotEmpty())
                    item {
                        Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                            SampleText(
                                    text = stringResource(R.string.stream_seasons),
                                style = CurrentTheme.typography.titleLarge
                            )
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                SampleButton(
                                    text = stringResource(
                                        R.string.stream_season,
                                        state.selectedSeason ?: details.seasons.first().number
                                    ),
                                    onClick = { expanded = true },
                                    style = SampleButtonStyle.OUTLINED
                                )
                                SampleDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    details.seasons.forEach { season ->
                                        SampleDropdownMenuItem(
                                            text = season.name,
                                            onClick = {
                                                expanded = false
                                                event(
                                                    DetailsUiEvent.SelectSeason(
                                                        number = season.number
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            if (state.seasonLoading && state.season == null) SampleCircularProgressIndicator()
                            state.season?.episodes?.forEach { episode ->
                                SampleText(
                                        text = stringResource(
                                        R.string.stream_episode,
                                        episode.number,
                                        episode.name
                                    ),
                                    style = CurrentTheme.typography.titleSmall
                                )
                                episode.airDate?.let {
                                    SampleText(text = it, style = CurrentTheme.typography.labelSmall)
                                }
                                episode.runtime?.let {
                                    SampleText(
                                            text = stringResource(R.string.stream_minutes, it),
                                        style = CurrentTheme.typography.labelSmall
                                    )
                                }
                                episode.overview?.takeIf(String::isNotBlank)?.let {
                                    SampleText(text = it, style = CurrentTheme.typography.bodySmall)
                                }
                                SampleDivider(
                                    Modifier.padding(vertical = CurrentTheme.spacing.small)
                                )
                            }
                        }
                    }
                if (details.cast.isNotEmpty())
                    item {
                        Column {
                            SampleText(
                                text = stringResource(R.string.stream_cast),
                                modifier = Modifier.padding(horizontal = CurrentTheme.spacing.large),
                                style = CurrentTheme.typography.titleLarge
                            )
                            LazyRow(
                                contentPadding = PaddingValues(CurrentTheme.spacing.large),
                                horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.medium)
                            ) {
                                items(details.cast) { person ->
                                    Column(Modifier.width(StreamDimens.portrait)) {
                                        SampleProfileImage(
                                            request = imageRequest(person.imagePath, ImageRole.PROFILE),
                                            contentDescription = person.name,
                                            modifier = Modifier.size(StreamDimens.portrait)
                                        )
                                        SampleText(
                                                text = person.name,
                                            style = CurrentTheme.typography.labelMedium
                                        )
                                        person.role?.let {
                                            SampleText(text = it, style = CurrentTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                item {
                    Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                        ExpandableInformation(stringResource(R.string.stream_information)) {
                            Info(R.string.stream_status, details.status)
                            Info(R.string.stream_votes, details.voteCount?.toString())
                            Info(R.string.stream_language, details.originalLanguage)
                            Info(R.string.stream_languages, details.languages.joinToString(", "))
                            Info(R.string.stream_countries, details.countries.joinToString(", "))
                            Info(R.string.stream_companies, details.companies.joinToString(", "))
                            details.homepage?.let {
                                SampleLink(url = it, label = stringResource(R.string.stream_homepage))
                            }
                        }
                        if (details.crew.isNotEmpty()) {
                            ExpandableInformation(stringResource(R.string.stream_crew)) {
                                details.crew.forEach { person ->
                                    SampleText(text = person.name, style = CurrentTheme.typography.titleSmall)
                                    person.role?.let {
                                        SampleText(text = it, style = CurrentTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                        if (details.videos.size > 1) {
                            ExpandableInformation(stringResource(R.string.stream_other_videos)) {
                                details.videos
                                    .filter { it.key != trailer?.key }
                                    .forEach { video ->
                                        SampleText(
                                                text = video.name,
                                            style = CurrentTheme.typography.titleSmall
                                        )
                                        if (video.site.equals("YouTube", true)) {
                                            SampleVideoPlayer(
                                                source = VideoSource(
                                                    provider = video.site,
                                                    key = video.key
                                                ),
                                                online = state.online
                                            )
                                        }
                                    }
                            }
                        }
                        if (details.budget != null || details.revenue != null) {
                            ExpandableInformation(stringResource(R.string.stream_financial)) {
                                Info(R.string.stream_budget, details.budget?.toString())
                                Info(R.string.stream_revenue, details.revenue?.toString())
                            }
                        }
                        SampleText(
                                text = stringResource(R.string.stream_tmdb_attribution),
                            style = CurrentTheme.typography.labelSmall,
                            modifier = Modifier.padding(vertical = CurrentTheme.spacing.large)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableInformation(title: String, content: @Composable () -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    SampleButton(text = title, onClick = { expanded = !expanded }, style = SampleButtonStyle.TEXT)
    if (expanded) content()
    SampleDivider()
}

@Preview(showBackground = true)
@Composable
private fun DetailsPreview() {
    ComposeSampleTheme { DetailsContent(DetailsUiState(loading = false), {}, {}) }
}
