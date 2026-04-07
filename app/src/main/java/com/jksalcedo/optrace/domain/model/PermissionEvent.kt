package com.jksalcedo.optrace.domain.model

data class PermissionEvent(
    val timestamp: Long,
    val uid: Int?,
    val packageName: String,
    val opName: String,              // e.g. OP_CAMERA
    val mode: String?,               // allow/ignore/foreground/...
    val attributionTag: String?,
    val source: EventSource,
    val confidence: EventConfidence,
    val rawRef: String? = null       // key to raw snapshot row/file
)

enum class EventSource { ROOT, SHIZUKU, FALLBACK }
enum class EventConfidence { DIRECT, INFERRED }
