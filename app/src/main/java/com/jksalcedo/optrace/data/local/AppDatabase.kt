package com.jksalcedo.optrace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PermissionEventEntity::class, SnapshotCacheEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun permissionEventDao(): PermissionEventDao
    abstract fun snapshotCacheDao(): SnapshotCacheDao
}
