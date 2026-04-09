package com.jksalcedo.optrace.domain.model

/**
 * Normalized permission/app-op timeline event.
 * Keep this immutable and storage-friendly.
 */
data class PermissionEvent(
    // When this event record was created (device epoch millis)
    val eventTimeMillis: Long,

    // When the source snapshot was captured (can differ from eventTimeMillis)
    val capturedAtMillis: Long,

    // Stable identity context
    val userId: Int? = null,
    val uid: Int? = null,
    val packageName: String,
    val appLabel: String? = null,

    // Op details
    val opName: String,
    val opCode: Int? = null,               // optional numeric op code when available
    val mode: String? = null,
    val attributionTag: String? = null,

    // Why this event was emitted
    val changeType: ChangeType,            // NEW_OP, MODE_CHANGED, LAST_ACCESS_INCREASED, ...
    val previousMode: String? = null,
    val currentMode: String? = mode,

    // Access timing from source (if available)
    val lastAccessTimeMillis: Long? = null,
    val lastRejectTimeMillis: Long? = null,
    val accessCount: Int? = null,          // optional if parsable

    // Provenance / trust
    val source: EventSource,
    val confidence: EventConfidence,
    val parserVersion: String? = null,

    // Correlation + diagnostics
    val snapshotId: String? = null,        // UUID/string id for capture batch
    val rawRef: String? = null,            // pointer to raw snapshot row/file
    val notes: String? = null              // optional debug/context note
)

enum class EventSource { ROOT, SHIZUKU, FALLBACK }
enum class EventConfidence { DIRECT, INFERRED }

enum class ChangeType {
    NEW_OP,
    MODE_CHANGED,
    LAST_ACCESS_INCREASED,
    LAST_REJECT_INCREASED,
    ATTRIBUTION_CHANGED
}