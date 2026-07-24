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
        val commands = listOf("cmd appops dump", "dumpsys appops", "appops dump")
        var stdout = ""
        var stderr = ""
        var success = false

        for (cmd in commands) {
            val res = shell.execute(cmd)
            if (res.exitCode == 0 && res.stdout.isNotBlank()) {
                stdout = res.stdout
                success = true
                break
            } else {
                stderr = res.stderr
            }
        }

        if (!success) {
            return AppOpsSnapshot(
                capturedAt = System.currentTimeMillis(),
                entries = emptyList(),
                rawText = "Failed to dump appops (stderr: $stderr)"
            )
        }
        return parser.parse(stdout, System.currentTimeMillis())
    }

    override fun source(): EventSource = EventSource.ROOT
}
