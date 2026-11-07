package br.com.composesample.structural.media

/** Identifies the app-owned storage area and relative directory for a media file. */
data class MediaLocation(
    val area: MediaStorageArea,
    val directory: String
) {

    companion object {
        /** Default directory inside the app's private media cache. */
        const val DEFAULT_CACHE_DIRECTORY = "media"

        /** Returns a media location in the app's private cache. */
        fun appCache(directory: String = DEFAULT_CACHE_DIRECTORY): MediaLocation =
            MediaLocation(MediaStorageArea.APP_CACHE, directory)

        /** Returns a media location in shared gallery storage. */
        fun gallery(directory: String): MediaLocation =
            MediaLocation(MediaStorageArea.GALLERY, directory)
    }
}
