package com.jksalcedo.optrace.domain.usecase

import android.util.Log
import com.jksalcedo.optrace.core.capability.CapabilityResolver
import com.jksalcedo.optrace.core.shell.AppOpsParser
import com.jksalcedo.optrace.data.local.PermissionEventDao
import com.jksalcedo.optrace.data.local.SnapshotCacheDao
import com.jksalcedo.optrace.data.local.SnapshotCacheEntity
import com.jksalcedo.optrace.data.local.toEntities
import com.jksalcedo.optrace.domain.model.CapabilityTier
import com.jksalcedo.optrace.domain.model.EventConfidence
import com.jksalcedo.optrace.domain.source.PermissionEventSource

class CollectPermissionEventsUseCase(
    private val resolver: CapabilityResolver,
    private val root: PermissionEventSource,
    private val shizuku: PermissionEventSource,
    private val fallback: PermissionEventSource,
    private val differ: DiffSnapshotsUseCase,
    private val eventDao: PermissionEventDao,
    private val snapshotDao: SnapshotCacheDao,
    private val parser: AppOpsParser
) {
    suspend operator fun invoke() {
        val cap = resolver.resolve()
        val src = when (cap.tier) {
            CapabilityTier.ROOT -> root
            CapabilityTier.SHIZUKU -> shizuku
            CapabilityTier.FALLBACK -> fallback
        }

        val current = src.captureSnapshot()
        if (current.entries.isEmpty()) {
            Log.w(TAG, "Empty snapshot from ${cap.tier}, skipping")
            return
        }

        val sourceKey = src.source().name
        val confidence = when (cap.tier) {
            CapabilityTier.ROOT, CapabilityTier.SHIZUKU -> EventConfidence.DIRECT
            CapabilityTier.FALLBACK -> EventConfidence.INFERRED
        }

        val cached = snapshotDao.get(sourceKey)
        val previous = cached?.rawText?.let { raw ->
            parser.parse(raw, cached.capturedAt)
        }

        val events = differ.diff(previous, current, src.source(), confidence)

        if (events.isNotEmpty()) {
            eventDao.insertAll(events.toEntities())
            Log.d(TAG, "Persisted ${events.size} events from $sourceKey")
        }

        current.rawText?.let { raw ->
            snapshotDao.upsert(
                SnapshotCacheEntity(
                    source = sourceKey,
                    rawText = raw,
                    capturedAt = current.capturedAt
                )
            )
        }
    }

    companion object {
        private const val TAG = "CollectEvents"
    }
}
