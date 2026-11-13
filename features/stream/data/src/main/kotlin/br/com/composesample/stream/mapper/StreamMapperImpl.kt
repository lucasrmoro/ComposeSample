package br.com.composesample.stream.mapper

import br.com.composesample.stream.dto.catalog.GenreRespDTO
import br.com.composesample.stream.dto.catalog.GenresRespDTO
import br.com.composesample.stream.dto.catalog.PageRespDTO
import br.com.composesample.stream.dto.credits.PersonRespDTO
import br.com.composesample.stream.dto.media.MediaRespDTO
import br.com.composesample.stream.dto.media.NamedRespDTO
import br.com.composesample.stream.dto.providers.ProviderRespDTO
import br.com.composesample.stream.dto.releases.ReleaseRespDTO
import br.com.composesample.stream.dto.seasons.EpisodeRespDTO
import br.com.composesample.stream.dto.seasons.SeasonRespDTO
import br.com.composesample.stream.dto.videos.VideoRespDTO
import br.com.composesample.stream.entity.StreamCacheEntity
import br.com.composesample.stream.model.catalog.CatalogPage
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.credits.PersonCredit
import br.com.composesample.stream.model.details.MediaDetails
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaSummary
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.model.providers.ProviderCategory
import br.com.composesample.stream.model.providers.WatchProvider
import br.com.composesample.stream.model.releases.ReleaseEvent
import br.com.composesample.stream.model.releases.ReleaseKind
import br.com.composesample.stream.model.seasons.EpisodeDetails
import br.com.composesample.stream.model.seasons.SeasonDetails
import br.com.composesample.stream.model.seasons.SeasonSummary
import br.com.composesample.stream.model.videos.VideoReference
import br.com.composesample.structural.network.http.JsonCodec
import br.com.composesample.structural.network.http.decode

class StreamMapperImpl(private val jsonCodec: JsonCodec) : StreamMapper {

    override fun page(entity: StreamCacheEntity?, query: CatalogQuery): CatalogPage {
        val page = pageDTO(entity) ?: return CatalogPage()
        return CatalogPage(
            items = page.results.map { summary(it, query.type, query.region) },
            hasNextPage = page.page < page.totalPages
        )
    }

    override fun pageDTO(entity: StreamCacheEntity?): PageRespDTO? = entity?.let {
        jsonCodec.decode<PageRespDTO>(it.payload)
    }

    override fun pageEntity(
        query: CatalogQuery,
        page: PageRespDTO,
        previous: PageRespDTO?
    ): StreamCacheEntity {
        val merged =
            if (previous == null) page
            else page.copy(results = (previous.results + page.results).distinctBy(MediaRespDTO::id))
        return entity(query.key, merged)
    }

    override fun entity(key: String, value: Any) =
        StreamCacheEntity(key = key, payload = jsonCodec.encode(value))

    private fun summary(dto: MediaRespDTO, type: MediaType, region: CatalogRegion): MediaSummary {
        val date = if (type == MediaType.MOVIE) dto.releaseDate else dto.firstAirDate
        return MediaSummary(
            id = MediaId(type = type, value = dto.id),
            title = dto.title ?: dto.name.orEmpty(),
            posterPath = dto.posterPath,
            year = date?.take(4)?.takeIf { it.length == 4 },
            rating = dto.voteAverage?.takeIf { (dto.voteCount ?: 0) > 0 },
            certification = certification(dto, type, region),
            releaseEvent = releaseEvent(dto)
        )
    }

    private fun certification(dto: MediaRespDTO, type: MediaType, region: CatalogRegion) =
        if (type == MediaType.MOVIE) releaseCertification(dto, region)
        else contentRating(dto, region)

    private fun releaseCertification(dto: MediaRespDTO, region: CatalogRegion) =
        dto.releaseDates
            ?.results
            ?.firstOrNull { it.country == region.countryCode }
            ?.dates
            ?.firstNotNullOfOrNull { it.certification?.takeIf(String::isNotBlank) }

    private fun contentRating(dto: MediaRespDTO, region: CatalogRegion) =
        dto.contentRatings
            ?.results
            ?.firstOrNull { it.country == region.countryCode }
            ?.rating
            ?.takeIf(String::isNotBlank)

    private fun releaseEvent(dto: MediaRespDTO): ReleaseEvent? =
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

    override fun genres(entity: StreamCacheEntity?): List<Genre> =
        entity?.let { jsonCodec.decode<GenresRespDTO>(it.payload).genres.map(::genre) }.orEmpty()

    private fun genre(dto: GenreRespDTO) = Genre(id = dto.id, name = dto.name)

    private fun person(dto: PersonRespDTO) =
        PersonCredit(name = dto.name, role = dto.character ?: dto.job, imagePath = dto.profilePath)

    private fun video(dto: VideoRespDTO) =
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
        val dto = entity?.let { jsonCodec.decode<MediaRespDTO>(it.payload) } ?: return null
        return mediaDetails(dto, id, region)
    }

    private fun mediaDetails(dto: MediaRespDTO, id: MediaId, region: CatalogRegion) =
        MediaDetails(
            summary = summary(dto, id.type, region),
            originalTitle = dto.originalTitle ?: dto.originalName,
            overview = dto.overview?.takeIf(String::isNotBlank),
            backdropPath = dto.backdropPath,
            genres = dto.genres.orEmpty().map(::genre),
            runtimeMinutes = dto.runtime?.takeIf { it > 0 },
            releaseDate = releaseDate(dto, region),
            status = dto.status,
            voteCount = dto.voteCount,
            originalLanguage = dto.originalLanguage,
            countries = dto.productionCountries.orEmpty().mapNotNull(NamedRespDTO::name),
            languages = dto.spokenLanguages.orEmpty().mapNotNull { it.name ?: it.englishName },
            companies = dto.productionCompanies.orEmpty().mapNotNull(NamedRespDTO::name),
            budget = dto.budget?.takeIf { it > 0 },
            revenue = dto.revenue?.takeIf { it > 0 },
            homepage = dto.homepage?.takeIf(String::isNotBlank),
            cast = dto.credits?.cast.orEmpty().map(::person),
            crew = dto.credits?.crew.orEmpty().map(::person),
            videos = videos(dto),
            providers = providers(dto, region),
            providerLink = dto.watchProviders?.results?.get(region.countryCode)?.link,
            seasons = seasons(dto),
            episodeCount = dto.numberOfEpisodes
        )

    private fun releaseDate(dto: MediaRespDTO, region: CatalogRegion) =
        regionalReleaseDate(dto, region) ?: dto.releaseDate ?: dto.firstAirDate

    private fun regionalReleaseDate(dto: MediaRespDTO, region: CatalogRegion) =
        dto.releaseDates
            ?.results
            ?.firstOrNull { it.country == region.countryCode }
            ?.dates
            ?.mapNotNull(ReleaseRespDTO::date)
            ?.minOrNull()
            ?.take(10)

    private fun providers(dto: MediaRespDTO, region: CatalogRegion): List<WatchProvider> {
        val providers = dto.watchProviders?.results?.get(region.countryCode) ?: return emptyList()
        return providerGroup(providers.subscription, ProviderCategory.SUBSCRIPTION) +
            providerGroup(providers.free, ProviderCategory.FREE) +
            providerGroup(providers.ads, ProviderCategory.ADS) +
            providerGroup(providers.rent, ProviderCategory.RENT) +
            providerGroup(providers.buy, ProviderCategory.BUY)
    }

    private fun providerGroup(items: List<ProviderRespDTO>?, category: ProviderCategory) =
        items.orEmpty().map {
            WatchProvider(
                id = it.id,
                name = it.name,
                logoPath = it.logoPath,
                category = category
            )
        }

    private fun videos(dto: MediaRespDTO) =
        dto.videos?.results.orEmpty()
            .map(::video)
            .sortedByDescending(VideoReference::official)

    private fun seasons(dto: MediaRespDTO) =
        dto.seasons.orEmpty()
            .filter { it.number > 0 }
            .map {
                SeasonSummary(
                    number = it.number,
                    name = it.name,
                    episodeCount = it.episodeCount,
                    airDate = it.airDate
                )
            }

    override fun season(entity: StreamCacheEntity?): SeasonDetails? {
        val dto = entity?.let { jsonCodec.decode<SeasonRespDTO>(it.payload) } ?: return null
        return SeasonDetails(
            number = dto.number,
            name = dto.name,
            overview = dto.overview,
            episodes = dto.episodes.orEmpty().map(::episode)
        )
    }

    private fun episode(dto: EpisodeRespDTO) =
        EpisodeDetails(
            number = dto.number,
            name = dto.name,
            overview = dto.overview,
            airDate = dto.airDate,
            runtime = dto.runtime,
            imagePath = dto.stillPath
        )

}
