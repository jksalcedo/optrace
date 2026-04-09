package com.jksalcedo.optrace.core.parser

import com.jksalcedo.optrace.core.shell.AppOpsParser
import com.jksalcedo.optrace.domain.model.AppOpsEntry
import com.jksalcedo.optrace.domain.model.AppOpsSnapshot

class AppOpsParserV1 : AppOpsParser {

    override fun parse(raw: String, capturedAt: Long): AppOpsSnapshot {
        var currentPkg: String? = null
        var currentUid: Int? = null
        val out = mutableListOf<AppOpsEntry>()

        raw.lineSequence().forEach { line ->
            val trimmed = line.trim()

            // Package + uid
            Regexes.pkgWithUid.find(trimmed)?.let { m ->
                currentUid = m.groupValues[1].toIntOrNull()
                currentPkg = m.groupValues[2]
                return@forEach
            }

            // Package only
            Regexes.pkgOnly.find(trimmed)?.let { m ->
                currentPkg = m.groupValues[1]
                // keep uid if not explicitly replaced
                return@forEach
            }

            // Op line
            val opMatch = Regexes.opLine.find(trimmed) ?: return@forEach
            val pkg = currentPkg ?: return@forEach

            val op = opMatch.groupValues[1]
            val mode = opMatch.groupValues.getOrNull(2)

            val lastAccess = Regexes.lastAccess.find(trimmed)
                ?.groupValues?.getOrNull(2)?.toLongOrNull()

            val lastReject = Regexes.lastReject.find(trimmed)
                ?.groupValues?.getOrNull(2)?.toLongOrNull()

            val attribution = Regexes.attribution.find(trimmed)
                ?.groupValues?.getOrNull(1)

            out += AppOpsEntry(
                uid = currentUid,
                packageName = pkg,
                opName = op,
                mode = mode,
                lastAccessTimeMillis = lastAccess,
                lastRejectTimeMillis = lastReject,
                attributionTag = attribution
            )
        }

        return AppOpsSnapshot(
            capturedAt = capturedAt,
            entries = out,
            rawText = raw
        )
    }
}