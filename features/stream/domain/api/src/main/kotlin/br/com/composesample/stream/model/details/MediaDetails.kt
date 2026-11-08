package br.com.composesample.stream.model.details

import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.credits.PersonCredit
import br.com.composesample.stream.model.media.MediaSummary
import br.com.composesample.stream.model.providers.WatchProvider
import br.com.composesample.stream.model.seasons.SeasonSummary
import br.com.composesample.stream.model.videos.VideoReference

data class MediaDetails(
    val summary: MediaSummary,
    val originalTitle: String?,
    val overview: String?,
    val backdropPath: String?,
    val genres: List<Genre>,
    val runtimeMinutes: Int?,
    val releaseDate: String?,
    val status: String?,
    val voteCount: Int?,
    val originalLanguage: String?,
    val countries: List<String>,
    val languages: List<String>,
    val companies: List<String>,
    val budget: Long?,
    val revenue: Long?,
    val homepage: String?,
    val cast: List<PersonCredit>,
    val crew: List<PersonCredit>,
    val videos: List<VideoReference>,
    val providers: List<WatchProvider>,
    val providerLink: String?,
    val seasons: List<SeasonSummary>,
    val episodeCount: Int?
)
