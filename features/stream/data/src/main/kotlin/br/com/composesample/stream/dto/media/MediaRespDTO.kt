package br.com.composesample.stream.dto.media

import br.com.composesample.stream.dto.catalog.GenreRespDTO
import br.com.composesample.stream.dto.credits.CreditsRespDTO
import br.com.composesample.stream.dto.providers.WatchProvidersRespDTO
import br.com.composesample.stream.dto.releases.EventRespDTO
import br.com.composesample.stream.dto.releases.ReleaseDatesRespDTO
import br.com.composesample.stream.dto.seasons.EpisodeRespDTO
import br.com.composesample.stream.dto.seasons.SeasonRespDTO
import br.com.composesample.stream.dto.videos.VideosRespDTO
import br.com.composesample.structural.network.http.JsonName

data class MediaRespDTO(
    @JsonName("id") val id: Long = 0,
    @JsonName("title") val title: String? = null,
    @JsonName("name") val name: String? = null,
    @JsonName("original_title") val originalTitle: String? = null,
    @JsonName("original_name") val originalName: String? = null,
    @JsonName("overview") val overview: String? = null,
    @JsonName("poster_path") val posterPath: String? = null,
    @JsonName("backdrop_path") val backdropPath: String? = null,
    @JsonName("release_date") val releaseDate: String? = null,
    @JsonName("first_air_date") val firstAirDate: String? = null,
    @JsonName("vote_average") val voteAverage: Double? = null,
    @JsonName("vote_count") val voteCount: Int? = null,
    @JsonName("genres") val genres: List<GenreRespDTO>? = null,
    @JsonName("runtime") val runtime: Int? = null,
    @JsonName("status") val status: String? = null,
    @JsonName("original_language") val originalLanguage: String? = null,
    @JsonName("production_countries") val productionCountries: List<NamedRespDTO>? = null,
    @JsonName("spoken_languages") val spokenLanguages: List<NamedRespDTO>? = null,
    @JsonName("production_companies") val productionCompanies: List<NamedRespDTO>? = null,
    @JsonName("budget") val budget: Long? = null,
    @JsonName("revenue") val revenue: Long? = null,
    @JsonName("homepage") val homepage: String? = null,
    @JsonName("credits") val credits: CreditsRespDTO? = null,
    @JsonName("videos") val videos: VideosRespDTO? = null,
    @JsonName("release_dates") val releaseDates: ReleaseDatesRespDTO? = null,
    @JsonName("content_ratings") val contentRatings: ContentRatingsRespDTO? = null,
    @JsonName("watch/providers") val watchProviders: WatchProvidersRespDTO? = null,
    @JsonName("seasons") val seasons: List<SeasonRespDTO>? = null,
    @JsonName("number_of_episodes") val numberOfEpisodes: Int? = null,
    @JsonName("next_episode_to_air") val nextEpisode: EpisodeRespDTO? = null,
    @JsonName("last_episode_to_air") val lastEpisode: EpisodeRespDTO? = null,
    @JsonName("stream_release_event") val event: EventRespDTO? = null
)
