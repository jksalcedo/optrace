package com.jksalcedo.optrace.core.shell

import com.jksalcedo.optrace.domain.model.AppOpsSnapshot

interface AppOpsParser {
    fun parse(raw: String, capturedAt: Long): AppOpsSnapshot
}
