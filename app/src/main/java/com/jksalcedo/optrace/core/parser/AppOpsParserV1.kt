package com.jksalcedo.optrace.core.parser

import android.util.Log
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
            if (trimmed.isEmpty()) return@forEach

            // Package + uid on same line
            Regexes.pkgWithUid.find(trimmed)?.let { m ->
                currentUid = m.groupValues[1].toIntOrNull()
                currentPkg = m.groupValues[2]
                return@forEach
            }

            // Package header line ("Package com.foo.bar:")
            Regexes.pkgOnly.find(trimmed)?.let { m ->
                currentPkg = m.groupValues[1]
                return@forEach
            }

            // Uid header line ("Uid 10234:")
            Regexes.uidOnly.find(trimmed)?.let { m ->
                currentUid = m.groupValues[1].toIntOrNull()
                return@forEach
            }

            // Top-level section reset (e.g. "Uids:", "Clients:", "User 0:")
            if (trimmed.equals("Uids:", ignoreCase = true) || trimmed.startsWith("User ", ignoreCase = true)) {
                currentPkg = null
                return@forEach
            }

            // Op line matching
            val opMatch = Regexes.opLineWithMode.find(trimmed)
                ?: Regexes.opLineSimple.find(trimmed)
                ?: return@forEach

            val pkg = currentPkg ?: currentUid?.let { "uid:$it" } ?: return@forEach

            val op = opMatch.groupValues[1]
            val mode = opMatch.groupValues.getOrNull(2)

            val lastAccessRaw = Regexes.lastAccess.find(trimmed)?.groupValues?.getOrNull(2)
            val lastAccess = parseTime(lastAccessRaw, capturedAt)

            val lastRejectRaw = Regexes.lastReject.find(trimmed)?.groupValues?.getOrNull(2)
            val lastReject = parseTime(lastRejectRaw, capturedAt)

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

        if (out.isEmpty() && raw.isNotBlank()) {
            Log.w(TAG, "Parsed 0 entries from non-blank stdout (${raw.length} chars). Raw output snippet:\n${raw.take(1500)}")
        } else {
            Log.d(TAG, "Parsed ${out.size} AppOps entries")
        }

        return AppOpsSnapshot(
            capturedAt = capturedAt,
            entries = out,
            rawText = raw
        )
    }

    private fun parseTime(raw: String?, now: Long): Long? {
        if (raw == null) return null
        raw.toLongOrNull()?.let { return it }

        val regex = Regex("""[+-]?(\d+)([smhd])""")
        val matches = regex.findAll(raw)
        var totalMillis = 0L
        for (m in matches) {
            val num = m.groupValues[1].toLongOrNull() ?: continue
            val unit = m.groupValues[2]
            val millis = when (unit) {
                "s" -> num * 1000
                "m" -> num * 60 * 1000
                "h" -> num * 3600 * 1000
                "d" -> num * 86400 * 1000
                else -> 0L
            }
            totalMillis += millis
        }
        return if (totalMillis > 0) now - totalMillis else null
    }

    companion object {
        private const val TAG = "AppOpsParserV1"
    }
}