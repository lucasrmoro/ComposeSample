package br.com.composesample.stream.mapper

import br.com.composesample.stream.dto.GenreDTO
import br.com.composesample.stream.dto.GenresDTO
import br.com.composesample.stream.dto.MediaDTO
import br.com.composesample.stream.dto.NamedDTO
import br.com.composesample.stream.dto.PageDTO
import br.com.composesample.stream.dto.PersonDTO
import br.com.composesample.stream.dto.ProviderDTO
import br.com.composesample.stream.dto.ReleaseDTO
import br.com.composesample.stream.dto.SeasonDTO
import br.com.composesample.stream.dto.VideoDTO
import br.com.composesample.stream.entity.StreamCacheEntity
import br.com.composesample.stream.model.CatalogPage
import br.com.composesample.stream.model.CatalogQuery
import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.EpisodeDetails
import br.com.composesample.stream.model.Genre
import br.com.composesample.stream.model.MediaDetails
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.model.MediaSummary
import br.com.composesample.stream.model.MediaType
import br.com.composesample.stream.model.PersonCredit
import br.com.composesample.stream.model.ProviderCategory
import br.com.composesample.stream.model.ReleaseEvent
import br.com.composesample.stream.model.ReleaseKind
import br.com.composesample.stream.model.SeasonDetails
import br.com.composesample.stream.model.SeasonSummary
import br.com.composesample.stream.model.VideoReference
import br.com.composesample.stream.model.WatchProvider
import br.com.composesample.structural.network.http.JsonCodec
import br.com.composesample.structural.network.http.decode

internal class StreamMapperImpl(private val jsonCodec: JsonCodec) : StreamMapper {

    override fun page(entity: StreamCacheEntity?, query: CatalogQuery): CatalogPage {
        val page = pageDTO(entity) ?: return CatalogPage()
        return CatalogPage(
            items = page.results.map { summary(it, query.type, query.region) },
            hasNextPage = page.page < page.totalPages
        )
    }

    override fun pageDTO(entity: StreamCacheEntity?): PageDTO? = entity?.let {
        jsonCodec.decode<PageDTO>(it.payload)
    }

    override fun pageEntity(
        query: CatalogQuery,
        page: PageDTO,
        previous: PageDTO?
    ): StreamCacheEntity {
        val merged =
            if (previous == null) page
            else page.copy(results = (previous.results + page.results).distinctBy(MediaDTO::id))
        return entity(query.key, merged)
    }

    override fun entity(key: String, value: Any) =
        StreamCacheEntity(key = key, payload = jsonCodec.encode(value))

    private fun summary(dto: MediaDTO, type: MediaType, region: CatalogRegion): MediaSummary {
        val certification =
            if (type == MediaType.MOVIE)
                dto.releaseDates
                    ?.results
                    ?.firstOrNull { it.country == region.countryCode }
                    ?.dates
                    ?.firstNotNullOfOrNull { it.certification?.takeIf(String::isNotBlank) }
            else
                dto.contentRatings
                    ?.results
                    ?.firstOrNull { it.country == region.countryCode }
                    ?.rating
                    ?.takeIf(String::isNotBlank)
        val date = if (type == MediaType.MOVIE) dto.releaseDate else dto.firstAirDate
        return MediaSummary(
            id = MediaId(type = type, value = dto.id),
            title = dto.title ?: dto.name.orEmpty(),
            posterPath = dto.posterPath,
            year = date?.take(4)?.takeIf { it.length == 4 },
            rating = dto.voteAverage?.takeIf { (dto.voteCount ?: 0) > 0 },
            certification = certification,
            releaseEvent =
                dto.event?.let { event ->
                    ReleaseKind.entries
                        .firstOrNull { it.name == event.kind }
                        ?.let {
                            ReleaseEvent(
                                kind = it,
                                date = event.date,
                                season = event.season,
                                episode = event.episode
                            )
                        }
                }
        )
    }

    override fun genres(entity: StreamCacheEntity?): List<Genre> =
        entity?.let { jsonCodec.decode<GenresDTO>(it.payload).genres.map(::genre) }.orEmpty()

    private fun genre(dto: GenreDTO) = Genre(id = dto.id, name = dto.name)

    private fun person(dto: PersonDTO) =
        PersonCredit(name = dto.name, role = dto.character ?: dto.job, imagePath = dto.profilePath)

    private fun video(dto: VideoDTO) =
        VideoReference(
            key = dto.key,
            site = dto.site,
            name = dto.name,
            type = dto.type,
            official = dto.official
        )

    override fun details(
        entity: StreamCacheEntity?,
        id: MediaId,
        region: CatalogRegion
    ): MediaDetails? {
        val dto = entity?.let { jsonCodec.decode<MediaDTO>(it.payload) } ?: return null
        val providers = dto.watchProviders?.results?.get(region.countryCode)
        fun group(items: List<ProviderDTO>?, category: ProviderCategory) =
            items.orEmpty().map {
                WatchProvider(
                    id = it.id,
                    name = it.name,
                    logoPath = it.logoPath,
                    category = category
                )
            }
        val available =
            group(providers?.subscription, ProviderCategory.SUBSCRIPTION) +
                group(providers?.free, ProviderCategory.FREE) +
                group(providers?.ads, ProviderCategory.ADS) +
                group(providers?.rent, ProviderCategory.RENT) +
                group(providers?.buy, ProviderCategory.BUY)
        val regionalDate =
            dto.releaseDates
                ?.results
                ?.firstOrNull { it.country == region.countryCode }
                ?.dates
                ?.mapNotNull(ReleaseDTO::date)
                ?.minOrNull()
                ?.take(10)
        return MediaDetails(
            summary = summary(dto, id.type, region),
            originalTitle = dto.originalTitle ?: dto.originalName,
            overview = dto.overview?.takeIf(String::isNotBlank),
            backdropPath = dto.backdropPath,
            genres = dto.genres.orEmpty().map(::genre),
            runtimeMinutes = dto.runtime?.takeIf { it > 0 },
            releaseDate = regionalDate ?: dto.releaseDate ?: dto.firstAirDate,
            status = dto.status,
            voteCount = dto.voteCount,
            originalLanguage = dto.originalLanguage,
            countries = dto.productionCountries.orEmpty().mapNotNull(NamedDTO::name),
            languages = dto.spokenLanguages.orEmpty().mapNotNull { it.name ?: it.englishName },
            companies = dto.productionCompanies.orEmpty().mapNotNull(NamedDTO::name),
            budget = dto.budget?.takeIf { it > 0 },
            revenue = dto.revenue?.takeIf { it > 0 },
            homepage = dto.homepage?.takeIf(String::isNotBlank),
            cast = dto.credits?.cast.orEmpty().map(::person),
            crew = dto.credits?.crew.orEmpty().map(::person),
            videos =
                dto.videos
                    ?.results
                    .orEmpty()
                    .map(::video)
                    .sortedByDescending(VideoReference::official),
            providers = available,
            providerLink = providers?.link,
            seasons =
                dto.seasons
                    .orEmpty()
                    .filter { it.number > 0 }
                    .map {
                        SeasonSummary(
                            number = it.number,
                            name = it.name,
                            episodeCount = it.episodeCount,
                            airDate = it.airDate
                        )
                    },
            episodeCount = dto.numberOfEpisodes
        )
    }

    override fun season(entity: StreamCacheEntity?): SeasonDetails? {
        val dto = entity?.let { jsonCodec.decode<SeasonDTO>(it.payload) } ?: return null
        return SeasonDetails(
            number = dto.number,
            name = dto.name,
            overview = dto.overview,
            episodes =
                dto.episodes.orEmpty().map {
                    EpisodeDetails(
                        number = it.number,
                        name = it.name,
                        overview = it.overview,
                        airDate = it.airDate,
                        runtime = it.runtime,
                        imagePath = it.stillPath
                    )
                }
        )
    }

}
