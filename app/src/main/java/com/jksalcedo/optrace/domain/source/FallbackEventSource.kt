package com.jksalcedo.optrace.domain.source

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import com.jksalcedo.optrace.domain.model.AppOpsEntry
import com.jksalcedo.optrace.domain.model.AppOpsSnapshot
import com.jksalcedo.optrace.domain.model.EventSource

class FallbackEventSource(private val context: Context) : PermissionEventSource {
    
    override suspend fun captureSnapshot(): AppOpsSnapshot {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val capturedAt = System.currentTimeMillis()
        val entries = mutableListOf<AppOpsEntry>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val opsToTrack = arrayOf(
                AppOpsManager.OPSTR_CAMERA,
                AppOpsManager.OPSTR_FINE_LOCATION,
                AppOpsManager.OPSTR_COARSE_LOCATION,
                AppOpsManager.OPSTR_RECORD_AUDIO,
                AppOpsManager.OPSTR_READ_CONTACTS,
                AppOpsManager.OPSTR_WRITE_CONTACTS,
                AppOpsManager.OPSTR_READ_EXTERNAL_STORAGE,
                AppOpsManager.OPSTR_WRITE_EXTERNAL_STORAGE
            )
        }

        return AppOpsSnapshot(
            capturedAt = capturedAt,
            entries = entries,
            rawText = if (entries.isEmpty()) "No AppOps history found (Requires Android Q+ or specific permissions)" else null
        )
    }

    private fun modeToString(mode: Int): String = when (mode) {
        AppOpsManager.MODE_ALLOWED -> "allow"
        AppOpsManager.MODE_IGNORED -> "ignore"
        AppOpsManager.MODE_ERRORED -> "error"
        AppOpsManager.MODE_DEFAULT -> "default"
        AppOpsManager.MODE_FOREGROUND -> "foreground"
        else -> "unknown($mode)"
    }

    override fun source(): EventSource = EventSource.FALLBACK
}
