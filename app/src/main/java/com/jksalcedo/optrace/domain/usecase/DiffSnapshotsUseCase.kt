package com.jksalcedo.optrace.domain.usecase

import com.jksalcedo.optrace.domain.model.AppOpsSnapshot
import com.jksalcedo.optrace.domain.model.EventSource
import com.jksalcedo.optrace.domain.model.PermissionEvent

class DiffSnapshotsUseCase {
    fun diff(
        previous: AppOpsSnapshot?,
        current: AppOpsSnapshot,
        source: EventSource
    ): List<PermissionEvent> {
        // v0 strategy:
        // key = packageName + opName + attributionTag
        // emit event if new key appears OR lastAccessTime increases OR mode changes
        return emptyList()
    }
}
