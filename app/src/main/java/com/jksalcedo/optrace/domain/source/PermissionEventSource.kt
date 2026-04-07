package com.jksalcedo.optrace.domain.source

import com.jksalcedo.optrace.domain.model.AppOpsSnapshot
import com.jksalcedo.optrace.domain.model.EventSource

interface PermissionEventSource {
    suspend fun captureSnapshot(): AppOpsSnapshot
    fun source(): EventSource
}
