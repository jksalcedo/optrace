package com.jksalcedo.optrace.domain.usecase

import com.jksalcedo.optrace.domain.model.AppOpsEntry
import com.jksalcedo.optrace.domain.model.AppOpsSnapshot
import com.jksalcedo.optrace.domain.model.ChangeType
import com.jksalcedo.optrace.domain.model.EventConfidence
import com.jksalcedo.optrace.domain.model.EventSource
import com.jksalcedo.optrace.domain.model.PermissionEvent

class DiffSnapshotsUseCase {
    fun diff(
        prev: AppOpsSnapshot?,
        curr: AppOpsSnapshot,
        source: EventSource,
        confidence: EventConfidence
    ): List<PermissionEvent> {
        val prevMap = prev?.entries.orEmpty().associateBy(::key)
        val out = mutableListOf<PermissionEvent>()

        for (e in curr.entries) {
            val p = prevMap[key(e)]
            val changeType = when {
                p == null -> ChangeType.NEW_OP
                p.mode != e.mode -> ChangeType.MODE_CHANGED
                increased(p.lastAccessTimeMillis, e.lastAccessTimeMillis) -> ChangeType.LAST_ACCESS_INCREASED
                else -> null
            } ?: continue

            out += PermissionEvent(
                eventTimeMillis = System.currentTimeMillis(),
                capturedAtMillis = curr.capturedAt,
                packageName = e.packageName,
                uid = e.uid,
                opName = e.opName,
                mode = e.mode,
                attributionTag = e.attributionTag,
                source = source,
                confidence = confidence,
                changeType = changeType,
                previousMode = p?.mode,
                lastAccessTimeMillis = e.lastAccessTimeMillis,
                lastRejectTimeMillis = e.lastRejectTimeMillis
            )
        }
        return out
    }

    private fun key(e: AppOpsEntry): String =
        "${e.packageName}|${e.opName}|${e.attributionTag.orEmpty()}"

    private fun increased(old: Long?, new: Long?): Boolean =
        old != null && new != null && new > old
}
