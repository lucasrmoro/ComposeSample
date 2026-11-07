package br.com.composesample.stream.model

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

data class PersonCredit(val name: String, val role: String?, val imagePath: String?)

data class VideoReference(
    val key: String,
    val site: String,
    val name: String,
    val type: String?,
    val official: Boolean
)

data class WatchProvider(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val category: ProviderCategory
)

enum class ProviderCategory {
    SUBSCRIPTION,
    FREE,
    ADS,
    RENT,
    BUY
}

data class SeasonSummary(
    val number: Int,
    val name: String,
    val episodeCount: Int?,
    val airDate: String?
)

data class EpisodeDetails(
    val number: Int,
    val name: String,
    val overview: String?,
    val airDate: String?,
    val runtime: Int?,
    val imagePath: String?
)

data class SeasonDetails(
    val number: Int,
    val name: String,
    val overview: String?,
    val episodes: List<EpisodeDetails>
)
