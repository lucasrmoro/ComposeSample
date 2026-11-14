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
import br.com.composesample.stream.model.catalog.CatalogPage
import br.com.composesample.stream.model.catalog.CatalogQuery
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
import br.com.composesample.structural.localization.AppRegion
import br.com.composesample.structural.network.http.JsonCodec
import br.com.composesample.structural.network.http.decode

internal class StreamMapperImpl(private val jsonCodec: JsonCodec) : StreamMapper {

    override fun page(payload: String?, query: CatalogQuery): CatalogPage {
        val page = pageDTO(payload) ?: return CatalogPage()
        return CatalogPage(
            items = page.results.map { summary(it, query.type, query.region) },
            hasNextPage = page.page < page.totalPages
        )
    }

    override fun pageDTO(payload: String?): PageRespDTO? =
        payload?.let { jsonCodec.decode<PageRespDTO>(it) }

    override fun pagePayload(
        query: CatalogQuery,
        page: PageRespDTO,
        previous: PageRespDTO?
    ): String {
        val merged =
            if (previous == null) page
            else page.copy(results = (previous.results + page.results).distinctBy(MediaRespDTO::id))
        return payload(merged)
    }

    override fun payload(value: Any): String = jsonCodec.encode(value)

    override fun details(payload: String?, id: MediaId, region: AppRegion): MediaDetails? =
        payload?.let { jsonCodec.decode<MediaRespDTO>(it) }?.let { mediaDetails(it, id, region) }

    override fun genres(payload: String?): List<Genre> =
        payload?.let { jsonCodec.decode<GenresRespDTO>(it).genres.map(::genre) }.orEmpty()

    override fun season(payload: String?): SeasonDetails? =
        payload?.let { jsonCodec.decode<SeasonRespDTO>(it) }?.let(::season)

    private fun summary(dto: MediaRespDTO, type: MediaType, region: AppRegion): MediaSummary {
        val date = if (type == MediaType.MOVIE) dto.releaseDate else dto.firstAirDate
        return MediaSummary(
            id = MediaId(type = type, value = dto.id),
            title = dto.title ?: dto.name.orEmpty(),
            posterPath = dto.posterPath,
            year = date?.take(4)?.takeIf { it.length == 4 },
            rating = dto.voteAverage?.takeIf { (dto.voteCount ?: 0) > 0 },
            certification = certification(dto, type, region)
        )
    }

    private fun certification(dto: MediaRespDTO, type: MediaType, region: AppRegion) =
        if (type == MediaType.MOVIE) releaseCertification(dto, region)
        else contentRating(dto, region)

    private fun releaseCertification(dto: MediaRespDTO, region: AppRegion) =
        dto.releaseDates
            ?.results
            ?.firstOrNull { it.country == region.countryCode }
            ?.dates
            ?.firstNotNullOfOrNull { it.certification?.takeIf(String::isNotBlank) }

    private fun contentRating(dto: MediaRespDTO, region: AppRegion) =
        dto.contentRatings
            ?.results
            ?.firstOrNull { it.country == region.countryCode }
            ?.rating
            ?.takeIf(String::isNotBlank)

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

    private fun mediaDetails(dto: MediaRespDTO, id: MediaId, region: AppRegion) =
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

    private fun releaseDate(dto: MediaRespDTO, region: AppRegion) =
        regionalReleaseDate(dto, region) ?: dto.releaseDate ?: dto.firstAirDate

    private fun regionalReleaseDate(dto: MediaRespDTO, region: AppRegion) =
        dto.releaseDates
            ?.results
            ?.firstOrNull { it.country == region.countryCode }
            ?.dates
            ?.mapNotNull(ReleaseRespDTO::date)
            ?.minOrNull()
            ?.take(10)

    private fun providers(dto: MediaRespDTO, region: AppRegion): List<WatchProvider> {
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

    private fun season(dto: SeasonRespDTO) =
        SeasonDetails(
            number = dto.number,
            name = dto.name,
            overview = dto.overview,
            episodes = dto.episodes.orEmpty().map(::episode)
        )

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

fun createStreamMapper(jsonCodec: JsonCodec): StreamMapper =
    StreamMapperImpl(jsonCodec = jsonCodec)
