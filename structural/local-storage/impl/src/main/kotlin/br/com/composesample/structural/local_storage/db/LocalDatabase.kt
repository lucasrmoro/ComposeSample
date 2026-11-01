package br.com.composesample.structural.local_storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.composesample.featureA.dao.SomeDao
import br.com.composesample.featureA.entity.SomeDbEntity

@TypeConverters(br.com.composesample.structural.local_storage.db.TypeConverters::class)
@Database(entities = [SomeDbEntity::class], version = 1, exportSchema = false)
internal abstract class LocalDatabase : RoomDatabase() {

    abstract fun fooDao(): SomeDao

}