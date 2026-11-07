package br.com.composesample.structural.local_storage.cache.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "cached_file_metadata",
    primaryKeys = ["namespace", "key"],
    indices = [Index(value = ["namespace", "lastUsedAt"])]
)
internal data class CachedFileMetadataEntity(
    val namespace: String,
    val key: String,
    val directory: String,
    val fileName: String,
    val extension: String,
    val mimeType: String,
    val sizeBytes: Long,
    val downloadedAt: Long,
    val lastUsedAt: Long
)
