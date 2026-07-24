package com.jksalcedo.optrace.data.local

import com.jksalcedo.optrace.domain.model.PermissionEvent
import java.util.UUID

fun List<PermissionEvent>.toEntities(snapshotId: String = UUID.randomUUID().toString()): List<PermissionEventEntity> =
    map { it.toEntity(snapshotId) }

fun PermissionEvent.toEntity(snapshotId: String): PermissionEventEntity =
    PermissionEventEntity(
        timestamp = eventTimeMillis,
        capturedAt = capturedAtMillis,
        snapshotId = snapshotId,
        parserVersion = parserVersion ?: "appops-v1",
        uid = uid,
        packageName = packageName,
        opName = opName,
        mode = mode,
        attributionTag = attributionTag,
        changeType = changeType.name,
        source = source.name,
        confidence = confidence.name,
        rawRef = rawRef
    )
