package com.jksalcedo.optrace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PermissionEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun permissionEventDao(): PermissionEventDao
}
