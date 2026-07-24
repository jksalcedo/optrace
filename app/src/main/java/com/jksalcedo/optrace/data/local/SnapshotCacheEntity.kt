package com.jksalcedo.optrace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "snapshot_cache")
data class SnapshotCacheEntity(
    @PrimaryKey val source: String,
    val rawText: String,
    val capturedAt: Long
)
