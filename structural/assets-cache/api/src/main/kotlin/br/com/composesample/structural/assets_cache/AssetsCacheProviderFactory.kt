package br.com.composesample.structural.assets_cache

/** Creates providers that share the cache infrastructure while retaining separate policies. */
interface AssetsCacheProviderFactory {

    fun create(configuration: AssetsCacheConfiguration): AssetsCacheProvider

}
