package com.jksalcedo.optrace.domain.source

import android.content.Context
import com.jksalcedo.optrace.domain.model.AppOpsSnapshot
import com.jksalcedo.optrace.domain.model.EventSource

class ShizukuEventSource(private val context: Context) : PermissionEventSource {
    override suspend fun captureSnapshot(): AppOpsSnapshot {
        // Shizuku pipeline not yet implemented — return empty snapshot instead of crashing.
        return AppOpsSnapshot(
            capturedAt = System.currentTimeMillis(),
            entries = emptyList(),
            rawText = "Shizuku source not yet implemented"
        )
    }

    override fun source(): EventSource = EventSource.SHIZUKU
}
