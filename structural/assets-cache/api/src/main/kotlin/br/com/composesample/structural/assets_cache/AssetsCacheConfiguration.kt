package br.com.composesample.structural.assets_cache

/**
 * Application-selected storage, capacity, refresh age. Age is
 * measured since the last successful download, independently of least recently accessed file
 * removal access time.
 */
data class AssetsCacheConfiguration(
    val directory: String,
    val maximumBytes: Long,
    val maximumAgeMillis: Long
) {

    init {
        require(directory.isNotBlank())
        require(maximumBytes > 0)
        require(maximumAgeMillis > 0)
    }

}
