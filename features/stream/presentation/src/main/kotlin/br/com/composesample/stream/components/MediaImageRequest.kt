package br.com.composesample.stream.components

import br.com.composesample.structural.assets_cache.AssetRequest

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
private const val BACKDROP_SIZE = "w780"
private const val POSTER_SIZE = "w342"
private const val PROFILE_SIZE = "w185"
private const val LOGO_SIZE = "w185"
private const val PATH_PREFIX = "/"
private const val PARENT_PATH = ".."

internal enum class ImageRole(val group: String) {
    POSTER("posters"),
    BACKDROP("backdrops"),
    PROFILE("cast"),
    LOGO("providers")
}

internal fun imageRequest(path: String?, role: ImageRole = ImageRole.POSTER): AssetRequest? =
    path
        ?.takeIf { it.startsWith(PATH_PREFIX) && !it.contains(PARENT_PATH) }
        ?.let {
            val size =
                when (role) {
                    ImageRole.POSTER -> POSTER_SIZE
                    ImageRole.BACKDROP -> BACKDROP_SIZE
                    ImageRole.PROFILE -> PROFILE_SIZE
                    ImageRole.LOGO -> LOGO_SIZE
                }
            AssetRequest(url = "$IMAGE_BASE_URL/$size$it", group = role.group)
        }
