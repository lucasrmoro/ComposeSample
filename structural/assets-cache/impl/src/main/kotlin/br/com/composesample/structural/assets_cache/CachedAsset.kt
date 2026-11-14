package br.com.composesample.structural.assets_cache

import br.com.composesample.structural.media.MediaFile

internal data class CachedAsset(val file: MediaFile, val downloadedAt: Long)
