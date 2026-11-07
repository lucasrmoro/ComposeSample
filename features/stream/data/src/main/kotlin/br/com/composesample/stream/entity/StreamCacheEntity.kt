package br.com.composesample.stream.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stream_cache")
data class StreamCacheEntity(@PrimaryKey val key: String, val payload: String)
