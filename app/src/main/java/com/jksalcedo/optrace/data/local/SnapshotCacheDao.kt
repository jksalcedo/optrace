package com.jksalcedo.optrace.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SnapshotCacheDao {
    @Upsert
    suspend fun upsert(entity: SnapshotCacheEntity)

    @Query("SELECT * FROM snapshot_cache WHERE source = :source LIMIT 1")
    suspend fun get(source: String): SnapshotCacheEntity?
}
