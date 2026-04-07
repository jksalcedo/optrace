package com.jksalcedo.optrace.domain.source

import com.jksalcedo.optrace.domain.model.AppOpsEntry
import com.jksalcedo.optrace.domain.model.AppOpsSnapshot
import com.jksalcedo.optrace.domain.model.EventSource

class FallbackEventSource : PermissionEventSource {
    override suspend fun captureSnapshot(): AppOpsSnapshot {
        // v0 dummy implementation:
        // Normally this would query AppOpsManager or shell dumpsys appops
        return AppOpsSnapshot(
            capturedAt = System.currentTimeMillis(),
            entries = listOf(
                AppOpsEntry(
                    uid = 1000,
                    packageName = "com.android.settings",
                    opName = "OP_READ_CONTACTS",
                    mode = "allow",
                    lastAccessTime = System.currentTimeMillis(),
                    attributionTag = null
                )
            ),
            rawText = "Dummy snapshot for UI verification"
        )
    }

    override fun source(): EventSource = EventSource.FALLBACK
}
