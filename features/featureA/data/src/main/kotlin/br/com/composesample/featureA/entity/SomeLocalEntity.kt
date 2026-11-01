package br.com.composesample.featureA.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "some_local_table")
data class SomeLocalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val someField: Any
)