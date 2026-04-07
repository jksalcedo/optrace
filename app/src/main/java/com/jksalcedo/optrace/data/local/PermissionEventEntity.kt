package com.jksalcedo.optrace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permission_events")
data class PermissionEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val uid: Int?,
    val packageName: String,
    val opName: String,
    val mode: String?,
    val attributionTag: String?,
    val source: String,
    val confidence: String,
    val rawRef: String?
)
