package com.jksalcedo.optrace.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PermissionEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(events: List<PermissionEventEntity>)

    @Query("SELECT * FROM permission_events ORDER BY timestamp DESC LIMIT :limit")
    fun observeLatest(limit: Int): Flow<List<PermissionEventEntity>>

    @Query("DELETE FROM permission_events WHERE timestamp < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)

    @Query("DELETE FROM permission_events")
    suspend fun deleteAll()
}
