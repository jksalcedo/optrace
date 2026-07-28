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
                increased(p.lastAccessTimeMillis, e.lastAccessTimeMillis) -> ChangeType.LAST_ACCESS_INCREASED
                increased(p.lastRejectTimeMillis, e.lastRejectTimeMillis) -> ChangeType.LAST_REJECT_INCREASED
                modesDiffer(p.mode, e.mode) -> ChangeType.MODE_CHANGED
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
        new != null && (old == null || new > old)

    private fun modesDiffer(oldMode: String?, newMode: String?): Boolean {
        if (oldMode == null || newMode == null) return false
        val normOld = normalizeMode(oldMode)
        val normNew = normalizeMode(newMode)
        return normOld != normNew
    }

    private fun normalizeMode(mode: String): String {
        val m = mode.lowercase().trim()
        return when {
            m == "allow" || m == "0" || m == "mode_allowed" -> "allow"
            m == "ignore" || m == "1" || m == "mode_ignored" -> "ignore"
            m == "errored" || m == "error" || m == "2" || m == "mode_errored" -> "error"
            m == "foreground" || m == "4" || m == "mode_foreground" -> "foreground"
            else -> m
        }
    }
}
