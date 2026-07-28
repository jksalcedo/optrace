package com.jksalcedo.optrace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permission_events")
data class PermissionEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val capturedAt: Long,
    val snapshotId: String,
    val parserVersion: String,

    val uid: Int?,
    val packageName: String,
    val opName: String,
    val mode: String?,
    val attributionTag: String?,

    val changeType: String,

    val source: String,    // ROOT / SHIZUKU / FALLBACK
    val confidence: String, // DIRECT / INFERRED
    val rawRef: String?
)