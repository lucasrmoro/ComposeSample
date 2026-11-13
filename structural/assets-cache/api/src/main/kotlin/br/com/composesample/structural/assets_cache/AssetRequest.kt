package br.com.composesample.structural.assets_cache

/** Selects an asset URL, its cache subdirectory, and the consumer's cache policy. */
data class AssetRequest(
    val url: String,
    val group: String,
    val configuration: AssetsCacheConfiguration
) {

    init {
        require(group.matches(GROUP_PATTERN))
    }

    private companion object {
        val GROUP_PATTERN = Regex("[a-z][a-z0-9-]*")
    }
}
