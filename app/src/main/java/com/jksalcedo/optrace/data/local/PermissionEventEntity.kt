package com.jksalcedo.optrace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permission_events")
data class PermissionEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,  // event time
    val capturedAt: Long,    // snapshot capture time
    val snapshotId: String,   // UUID per run
    val parserVersion: String, // e.g. "appops-v0"

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