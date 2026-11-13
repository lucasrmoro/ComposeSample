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
import br.com.composesample.stream.model.credits.PersonCredit
import br.com.composesample.stream.model.details.MediaDetails
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaSummary
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.model.providers.ProviderCategory
import br.com.composesample.stream.model.providers.WatchProvider
import br.com.composesample.stream.model.seasons.EpisodeDetails
import br.com.composesample.stream.model.seasons.SeasonDetails
import br.com.composesample.stream.model.seasons.SeasonSummary
import br.com.composesample.stream.model.videos.VideoReference
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
    private val back: () -> Unit,
) : BaseScreen<DetailsUiState, DetailsUiEffect>(detailsViewModel) {

    @Composable
    override fun Content(uiState: DetailsUiState) {
        LaunchedEffect(id) { detailsViewModel.sendEvent(DetailsUiEvent.Start(id)) }
        DetailsContent(state = uiState, back = back, event = detailsViewModel::sendEvent)
    }

    override fun consumeEffect(effect: DetailsUiEffect) = Unit
}

@Composable
private fun Info(label: Int, value: String?) {
    if (!value.isNullOrBlank())
        Column(modifier = Modifier.padding(vertical = CurrentTheme.spacing.xSmall)) {
            SampleText(
                text = stringResource(label),
                style = CurrentTheme.typography.labelMedium,
                color = CurrentTheme.color.onSurface,
            )
            SampleText(text = value, style = CurrentTheme.typography.bodyMedium)
        }
}

@Composable
private fun DetailsContent(
    state: DetailsUiState,
    back: () -> Unit,
    event: (DetailsUiEvent) -> Unit,
) {
    SampleScreen(
        title = stringResource(R.string.stream_details),
        canNavigateBack = true,
        onNavigateBack = back,
    ) {
        val mediaDetails = state.details
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small),
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                    LoadNotice(
                        loading = state.loading,
                        empty = mediaDetails == null,
                        error = state.error,
                        retry = { event(DetailsUiEvent.Retry) },
                    )
                }
            }
            mediaDetails?.let { details ->
                item { DetailsSummary(details = details) }
                details.overview?.let { overview -> item { SynopsisSection(overview = overview) } }
                val trailer = details.trailer()
                trailer?.let { trailer ->
                    item { TrailerSection(trailer = trailer, online = state.online) }
                }
                if (details.providers.isNotEmpty())
                    item { WatchProvidersSection(details = details) }
                if (details.seasons.isNotEmpty()) {
                    item { SeasonsSection(details = details, state = state, onEvent = event) }
                }
                if (details.cast.isNotEmpty()) item { CastSection(cast = details.cast) }
                item {
                    DetailsInformationSection(
                        details = details,
                        trailer = trailer,
                        online = state.online,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsSummary(details: MediaDetails) {
    Column(verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small)) {
        SampleImage(
            request = imageRequest(details.backdropPath, ImageRole.BACKDROP),
            contentDescription = details.summary.title,
            modifier = Modifier.fillMaxWidth().height(StreamDimens.backdropHeight),
        )
        Column(modifier = Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
            SampleText(text = details.summary.title, style = CurrentTheme.typography.headlineSmall)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small),
            ) {
                details.summary.rating?.let {
                    SampleText(
                        text = String.format(java.util.Locale.getDefault(), RATING_FORMAT, it),
                        style = CurrentTheme.typography.labelMedium,
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
                style = CurrentTheme.typography.bodySmall,
            )
            Info(
                label = R.string.stream_runtime,
                value = details.runtimeMinutes?.let { stringResource(R.string.stream_minutes, it) },
            )
            Info(label = R.string.stream_release_date, value = details.releaseDate)
            Info(label = R.string.stream_original_title, value = details.originalTitle)
        }
    }
}

@Composable
private fun SynopsisSection(overview: String) {
    Column(modifier = Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
        SampleText(
            text = stringResource(R.string.stream_synopsis),
            style = CurrentTheme.typography.titleLarge,
        )
        SampleText(text = overview)
    }
}

@Composable
private fun TrailerSection(trailer: VideoReference, online: Boolean) {
    Column(modifier = Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
        SampleText(
            text = stringResource(R.string.stream_trailer),
            style = CurrentTheme.typography.titleLarge,
        )
        SampleVideoPlayer(
            source = VideoSource(provider = trailer.site, key = trailer.key),
            online = online,
        )
        SampleText(text = trailer.name, style = CurrentTheme.typography.bodySmall)
    }
}

@Composable
private fun WatchProvidersSection(details: MediaDetails) {
    Column(modifier = Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
        SampleText(
            text = stringResource(R.string.stream_watch),
            style = CurrentTheme.typography.titleLarge,
        )
        details.providers.groupBy(WatchProvider::category).forEach { (category, providers) ->
            SampleText(
                text = stringResource(category.titleResource()),
                style = CurrentTheme.typography.titleSmall,
            )
            providers.forEach { provider ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small),
                ) {
                    SampleImage(
                        request = imageRequest(path = provider.logoPath, role = ImageRole.LOGO),
                        contentDescription = provider.name,
                        modifier = Modifier.size(CurrentTheme.size.xxxLarge),
                    )
                    SampleText(text = provider.name)
                }
            }
        }
        SampleText(
            text = stringResource(R.string.stream_justwatch),
            style = CurrentTheme.typography.labelSmall,
        )
        details.providerLink?.let {
            SampleLink(url = it, label = stringResource(R.string.stream_watch))
        }
    }
}

@Composable
private fun SeasonsSection(
    details: MediaDetails,
    state: DetailsUiState,
    onEvent: (DetailsUiEvent) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
        SampleText(
            text = stringResource(R.string.stream_seasons),
            style = CurrentTheme.typography.titleLarge,
        )
        var expanded by remember { mutableStateOf(false) }
        Box {
            SampleButton(
                text =
                    stringResource(
                        R.string.stream_season,
                        state.selectedSeason ?: details.seasons.first().number,
                    ),
                onClick = { expanded = true },
                style = SampleButtonStyle.OUTLINED,
            )
            SampleDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                details.seasons.forEach { season ->
                    SampleDropdownMenuItem(
                        text = season.name,
                        onClick = {
                            expanded = false
                            onEvent(DetailsUiEvent.SelectSeason(number = season.number))
                        },
                    )
                }
            }
        }
        if (state.seasonLoading && state.season == null) SampleCircularProgressIndicator()
        state.season?.episodes?.forEach { episode ->
            SampleText(
                text = stringResource(R.string.stream_episode, episode.number, episode.name),
                style = CurrentTheme.typography.titleSmall,
            )
            episode.airDate?.let {
                SampleText(text = it, style = CurrentTheme.typography.labelSmall)
            }
            episode.runtime?.let {
                SampleText(
                    text = stringResource(R.string.stream_minutes, it),
                    style = CurrentTheme.typography.labelSmall,
                )
            }
            episode.overview?.takeIf(String::isNotBlank)?.let {
                SampleText(text = it, style = CurrentTheme.typography.bodySmall)
            }
            SampleDivider(modifier = Modifier.padding(vertical = CurrentTheme.spacing.small))
        }
    }
}

@Composable
private fun CastSection(cast: List<PersonCredit>) {
    Column(modifier = Modifier) {
        SampleText(
            text = stringResource(R.string.stream_cast),
            modifier = Modifier.padding(horizontal = CurrentTheme.spacing.large),
            style = CurrentTheme.typography.titleLarge,
        )
        LazyRow(
            contentPadding = PaddingValues(CurrentTheme.spacing.large),
            horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.medium),
        ) {
            items(cast) { person ->
                Column(modifier = Modifier.width(StreamDimens.portrait)) {
                    SampleProfileImage(
                        request = imageRequest(person.imagePath, ImageRole.PROFILE),
                        contentDescription = person.name,
                        modifier = Modifier.size(StreamDimens.portrait),
                    )
                    SampleText(text = person.name, style = CurrentTheme.typography.labelMedium)
                    person.role?.let {
                        SampleText(text = it, style = CurrentTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsInformationSection(
    details: MediaDetails,
    trailer: VideoReference?,
    online: Boolean,
) {
    Column(modifier = Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
        ExpandableInformation(title = stringResource(R.string.stream_information)) {
            Info(label = R.string.stream_status, value = details.status)
            Info(label = R.string.stream_votes, value = details.voteCount?.toString())
            Info(label = R.string.stream_language, value = details.originalLanguage)
            Info(label = R.string.stream_languages, value = details.languages.joinToString(", "))
            Info(label = R.string.stream_countries, value = details.countries.joinToString(", "))
            Info(label = R.string.stream_companies, value = details.companies.joinToString(", "))
            details.homepage?.let {
                SampleLink(url = it, label = stringResource(R.string.stream_homepage))
            }
        }
        if (details.crew.isNotEmpty()) {
            ExpandableInformation(title = stringResource(R.string.stream_crew)) {
                details.crew.forEach { person ->
                    SampleText(text = person.name, style = CurrentTheme.typography.titleSmall)
                    person.role?.let {
                        SampleText(text = it, style = CurrentTheme.typography.bodySmall)
                    }
                }
            }
        }
        if (details.videos.size > 1) {
            ExpandableInformation(title = stringResource(R.string.stream_other_videos)) {
                details.videos
                    .filter { it.key != trailer?.key }
                    .forEach { video ->
                        SampleText(text = video.name, style = CurrentTheme.typography.titleSmall)
                        if (video.site.equals("YouTube", true)) {
                            SampleVideoPlayer(
                                source = VideoSource(provider = video.site, key = video.key),
                                online = online,
                            )
                        }
                    }
            }
        }
        (details.budget ?: details.revenue)?.let { _ ->
            ExpandableInformation(title = stringResource(R.string.stream_financial)) {
                Info(label = R.string.stream_budget, value = details.budget?.toString())
                Info(label = R.string.stream_revenue, value = details.revenue?.toString())
            }
        }
        SampleText(
            text = stringResource(R.string.stream_tmdb_attribution),
            style = CurrentTheme.typography.labelSmall,
            modifier = Modifier.padding(vertical = CurrentTheme.spacing.large),
        )
    }
}

private fun MediaDetails.trailer() =
    videos.firstOrNull { it.site.equals("YouTube", true) && it.type == "Trailer" }
        ?: videos.firstOrNull { it.site.equals("YouTube", true) }

private fun ProviderCategory.titleResource() =
    when (this) {
        ProviderCategory.SUBSCRIPTION -> R.string.stream_subscription
        ProviderCategory.FREE -> R.string.stream_free
        ProviderCategory.ADS -> R.string.stream_ads
        ProviderCategory.RENT -> R.string.stream_rent
        ProviderCategory.BUY -> R.string.stream_buy
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
    ComposeSampleTheme {
        DetailsContent(
            state =
                DetailsUiState(
                    details = previewMediaDetails(),
                    season = previewSeasonDetails(),
                    selectedSeason = 1,
                    loading = false,
                ),
            back = {},
            event = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoPreview() {
    ComposeSampleTheme { Info(label = R.string.stream_runtime, value = "45 minutes") }
}

@Preview(showBackground = true)
@Composable
private fun DetailsSummaryPreview() {
    ComposeSampleTheme { DetailsSummary(details = previewMediaDetails()) }
}

@Preview(showBackground = true)
@Composable
private fun SynopsisSectionPreview() {
    ComposeSampleTheme { SynopsisSection(overview = "A story about an unexpected journey.") }
}

@Preview(showBackground = true)
@Composable
private fun TrailerSectionPreview() {
    ComposeSampleTheme {
        previewMediaDetails().trailer()?.let { trailer ->
            TrailerSection(trailer = trailer, online = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WatchProvidersSectionPreview() {
    ComposeSampleTheme { WatchProvidersSection(details = previewMediaDetails()) }
}

@Preview(showBackground = true)
@Composable
private fun SeasonsSectionPreview() {
    ComposeSampleTheme {
        SeasonsSection(
            details = previewMediaDetails(),
            state =
                DetailsUiState(
                    season = previewSeasonDetails(),
                    selectedSeason = 1,
                    loading = false,
                ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CastSectionPreview() {
    ComposeSampleTheme { CastSection(cast = previewMediaDetails().cast) }
}

@Preview(showBackground = true)
@Composable
private fun DetailsInformationSectionPreview() {
    ComposeSampleTheme {
        val details = previewMediaDetails()
        DetailsInformationSection(details = details, trailer = details.trailer(), online = false)
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandableInformationPreview() {
    ComposeSampleTheme {
        ExpandableInformation(title = "Additional information") {
            SampleText(text = "Example content")
        }
    }
}

private fun previewMediaDetails() =
    MediaDetails(
        summary =
            MediaSummary(
                id = MediaId(type = MediaType.SERIES, value = 1L),
                title = "Example series",
                posterPath = null,
                year = "2024",
                rating = 8.2,
                certification = "TV-14",
                releaseEvent = null,
            ),
        originalTitle = "Example series",
        overview = "A story about an unexpected journey.",
        backdropPath = null,
        genres = listOf(Genre(id = 1, name = "Drama")),
        runtimeMinutes = 45,
        releaseDate = "2024-01-01",
        status = "Returning Series",
        voteCount = 250,
        originalLanguage = "en",
        countries = listOf("US"),
        languages = listOf("English"),
        companies = listOf("Example Studio"),
        budget = 1_000_000L,
        revenue = null,
        homepage = "https://example.com",
        cast = listOf(PersonCredit(name = "Alex Example", role = "Lead", imagePath = null)),
        crew = listOf(PersonCredit(name = "Sam Example", role = "Director", imagePath = null)),
        videos =
            listOf(
                VideoReference(
                    key = "preview-trailer",
                    site = "YouTube",
                    name = "Official trailer",
                    type = "Trailer",
                    official = true,
                ),
                VideoReference(
                    key = "preview-behind-scenes",
                    site = "YouTube",
                    name = "Behind the scenes",
                    type = "Behind the Scenes",
                    official = true,
                ),
            ),
        providers =
            listOf(
                WatchProvider(
                    id = 1,
                    name = "Example Video",
                    logoPath = null,
                    category = ProviderCategory.SUBSCRIPTION,
                )
            ),
        providerLink = "https://example.com/watch",
        seasons =
            listOf(SeasonSummary(number = 1, name = "Season 1", episodeCount = 1, airDate = null)),
        episodeCount = 1,
    )

private fun previewSeasonDetails() =
    SeasonDetails(
        number = 1,
        name = "Season 1",
        overview = null,
        episodes =
            listOf(
                EpisodeDetails(
                    number = 1,
                    name = "Episode 1",
                    overview = "An opening episode.",
                    airDate = "2024-01-01",
                    runtime = 45,
                    imagePath = null,
                )
            ),
    )
