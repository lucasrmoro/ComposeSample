package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class MediaDTO(
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
    @JsonName("genres") val genres: List<GenreDTO>? = null,
    @JsonName("runtime") val runtime: Int? = null,
    @JsonName("status") val status: String? = null,
    @JsonName("original_language") val originalLanguage: String? = null,
    @JsonName("production_countries") val productionCountries: List<NamedDTO>? = null,
    @JsonName("spoken_languages") val spokenLanguages: List<NamedDTO>? = null,
    @JsonName("production_companies") val productionCompanies: List<NamedDTO>? = null,
    @JsonName("budget") val budget: Long? = null,
    @JsonName("revenue") val revenue: Long? = null,
    @JsonName("homepage") val homepage: String? = null,
    @JsonName("credits") val credits: CreditsDTO? = null,
    @JsonName("videos") val videos: VideosDTO? = null,
    @JsonName("release_dates") val releaseDates: ReleaseDatesDTO? = null,
    @JsonName("content_ratings") val contentRatings: ContentRatingsDTO? = null,
    @JsonName("watch/providers") val watchProviders: WatchProvidersDTO? = null,
    @JsonName("seasons") val seasons: List<SeasonDTO>? = null,
    @JsonName("number_of_episodes") val numberOfEpisodes: Int? = null,
    @JsonName("next_episode_to_air") val nextEpisode: EpisodeDTO? = null,
    @JsonName("last_episode_to_air") val lastEpisode: EpisodeDTO? = null,
    @JsonName("stream_release_event") val event: EventDTO? = null
)
