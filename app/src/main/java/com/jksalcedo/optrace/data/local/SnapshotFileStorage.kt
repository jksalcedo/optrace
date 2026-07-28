package com.jksalcedo.optrace.data.local

import android.content.Context
import java.io.File

data class CachedSnapshot(
    val rawText: String,
    val capturedAt: Long
)

class SnapshotFileStorage(private val context: Context) {

    fun save(source: String, rawText: String, capturedAt: Long) {
        runCatching {
            val file = File(context.cacheDir, "snapshot_$source.txt")
            val metaFile = File(context.cacheDir, "snapshot_$source.meta")
            file.writeText(rawText)
            metaFile.writeText(capturedAt.toString())
        }
    }

    fun load(source: String): CachedSnapshot? {
        return runCatching {
            val file = File(context.cacheDir, "snapshot_$source.txt")
            val metaFile = File(context.cacheDir, "snapshot_$source.meta")
            if (!file.exists()) return null
            val rawText = file.readText()
            val capturedAt = metaFile.takeIf { it.exists() }?.readText()?.toLongOrNull() ?: file.lastModified()
            CachedSnapshot(rawText, capturedAt)
        }.getOrNull()
    }
}
