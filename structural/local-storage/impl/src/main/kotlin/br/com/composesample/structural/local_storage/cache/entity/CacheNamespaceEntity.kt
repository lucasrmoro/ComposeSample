package br.com.composesample.structural.local_storage.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_namespaces")
internal data class CacheNamespaceEntity(@PrimaryKey val namespace: String)
