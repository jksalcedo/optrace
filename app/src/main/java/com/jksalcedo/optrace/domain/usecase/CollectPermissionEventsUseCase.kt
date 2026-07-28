package com.jksalcedo.optrace.domain.usecase

import android.util.Log
import com.jksalcedo.optrace.core.capability.CapabilityResolver
import com.jksalcedo.optrace.core.shell.AppOpsParser
import com.jksalcedo.optrace.data.local.PermissionEventDao
import com.jksalcedo.optrace.data.local.SnapshotFileStorage
import com.jksalcedo.optrace.data.local.toEntities
import com.jksalcedo.optrace.domain.model.CapabilityTier
import com.jksalcedo.optrace.domain.model.EventConfidence
import com.jksalcedo.optrace.domain.source.PermissionEventSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CollectPermissionEventsUseCase(
    private val resolver: CapabilityResolver,
    private val root: PermissionEventSource,
    private val shizuku: PermissionEventSource,
    private val fallback: PermissionEventSource,
    private val differ: DiffSnapshotsUseCase,
    private val eventDao: PermissionEventDao,
    private val snapshotStorage: SnapshotFileStorage,
    private val parser: AppOpsParser
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val cap = resolver.resolve()
        val src = when (cap.tier) {
            CapabilityTier.ROOT -> root
            CapabilityTier.SHIZUKU -> shizuku
            CapabilityTier.FALLBACK -> fallback
        }

        val current = src.captureSnapshot()
        if (current.entries.isEmpty()) {
            Log.w(TAG, "Empty snapshot from ${cap.tier}, skipping")
            return@withContext
        }

        val sourceKey = src.source().name
        val confidence = when (cap.tier) {
            CapabilityTier.ROOT, CapabilityTier.SHIZUKU -> EventConfidence.DIRECT
            CapabilityTier.FALLBACK -> EventConfidence.INFERRED
        }

        val cached = snapshotStorage.load(sourceKey)
        val previous = cached?.rawText?.let { raw ->
            parser.parse(raw, cached.capturedAt)
        }

        val rawEvents = differ.diff(previous, current, src.source(), confidence)

        // On initial baseline scan (previous == null), filter out static entries without access timestamps to avoid DB bloat
        val eventsToPersist = if (previous == null) {
            rawEvents.filter { it.lastAccessTimeMillis != null || it.lastRejectTimeMillis != null }
        } else {
            rawEvents
        }

        if (eventsToPersist.isNotEmpty()) {
            val entities = eventsToPersist.toEntities()
            entities.chunked(500).forEach { chunk ->
                eventDao.insertAll(chunk)
            }
            Log.d(TAG, "Persisted ${eventsToPersist.size} events from $sourceKey (chunked insert)")
        }

        current.rawText?.let { raw ->
            snapshotStorage.save(sourceKey, raw, current.capturedAt)
        }
    }

    companion object {
        private const val TAG = "CollectEvents"
    }
}
