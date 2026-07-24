package com.jksalcedo.optrace.domain.source

import com.jksalcedo.optrace.core.shell.AppOpsParser
import com.jksalcedo.optrace.core.shell.ShellExecutor
import com.jksalcedo.optrace.domain.model.AppOpsSnapshot
import com.jksalcedo.optrace.domain.model.EventSource

class RootEventSource(
    private val shell: ShellExecutor,
    private val parser: AppOpsParser
) : PermissionEventSource {

    override suspend fun captureSnapshot(): AppOpsSnapshot {
        val result = shell.execute("appops get")
        if (result.exitCode != 0) {
            return AppOpsSnapshot(
                capturedAt = System.currentTimeMillis(),
                entries = emptyList(),
                rawText = "appops get failed (exit=${result.exitCode}): ${result.stderr}"
            )
        }
        return parser.parse(result.stdout, System.currentTimeMillis())
    }

    override fun source(): EventSource = EventSource.ROOT
}
