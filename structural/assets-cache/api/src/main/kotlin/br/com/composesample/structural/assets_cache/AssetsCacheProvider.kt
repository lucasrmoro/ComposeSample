package br.com.composesample.structural.assets_cache

import br.com.composesample.structural.media.MediaFile
import kotlinx.coroutines.flow.Flow

/** Local asset state; an expired asset stays available when refresh fails. */
data class AssetState(val file: MediaFile?, val isRefreshing: Boolean, val failed: Boolean)

/** Observes a local asset and refreshes it when missing or expired. */
interface AssetsCacheProvider {

    fun observe(request: AssetRequest, previousUrl: String? = null): Flow<AssetState>

}
