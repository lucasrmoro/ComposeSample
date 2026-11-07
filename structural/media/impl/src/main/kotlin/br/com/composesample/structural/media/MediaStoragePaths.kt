package br.com.composesample.structural.media

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import kotlinx.coroutines.flow.single

internal class MediaStoragePaths(private val context: Context) {

    fun getDirectory(location: MediaLocation): File {
        val parts = requireRelativePath(location.directory)
        return when (location.area) {
            MediaStorageArea.APP_CACHE -> File(
                File(context.cacheDir, MediaLocation.DEFAULT_CACHE_DIRECTORY),
                parts.joinToString("/")
            )
            MediaStorageArea.GALLERY -> {
                val rootName = parts.firstOrNull()
                if (rootName != Environment.DIRECTORY_PICTURES && rootName != Environment.DIRECTORY_MOVIES) {
                    throw IOException("Gallery media must be stored under Pictures or Movies")
                }
                val root = Environment.getExternalStoragePublicDirectory(rootName)
                File(
                    root,
                    parts.drop(GALLERY_ROOT_DIRECTORY_SEGMENT_COUNT).joinToString("/")
                )
            }
        }
    }

    fun requireName(value: String): String {
        val name = value.trim()
        require(name.isNotEmpty() && name != "." && name != ".." &&
            '/' !in name && '\\' !in name && '.' !in name
        ) { "Media names must be a single base name without an extension" }
        return name
    }

    fun requireExtension(value: String): String {
        val extension = value.removePrefix(".")
        require(extension.isNotBlank() && extension.all { it.isLetterOrDigit() }) {
            "Media extensions may contain only letters and digits"
        }
        return extension
    }

    private fun requireRelativePath(value: String): List<String> {
        val parts = value.split('/').filter(String::isNotBlank)
        require(parts.isNotEmpty() && parts.none { it == "." || it == ".." }) {
            "Media locations must use a non-empty relative directory"
        }
        return parts
    }

    fun requireGalleryPath(value: String): String {
        val parts = requireRelativePath(value)
        require(parts.first() == Environment.DIRECTORY_PICTURES ||
            parts.first() == Environment.DIRECTORY_MOVIES
        ) { "Gallery media must be stored under Pictures or Movies" }
        return parts.joinToString("/")
    }

    private companion object {
        const val GALLERY_ROOT_DIRECTORY_SEGMENT_COUNT = 1
    }

}
