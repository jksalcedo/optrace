package com.jksalcedo.optrace.domain.model

data class AppOpsSnapshot(
    val capturedAt: Long,
    val entries: List<AppOpsEntry>,
    val rawText: String? = null
)

data class AppOpsEntry(
    val uid: Int?,
    val packageName: String,
    val opName: String,
    val mode: String?,
    val lastAccessTime: Long?,
    val attributionTag: String?
)
