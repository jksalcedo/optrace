package com.jksalcedo.optrace.domain.source

import android.util.Log
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
                Log.d(TAG, "Successfully executed '$cmd', output size: ${stdout.length} chars")
                break
            } else {
                stderr = res.stderr
                Log.w(TAG, "Command '$cmd' failed (exit=${res.exitCode}): $stderr")
            }
        }

        if (!success) {
            Log.e(TAG, "All root appops dump commands failed. Last error: $stderr")
            return AppOpsSnapshot(
                capturedAt = System.currentTimeMillis(),
                entries = emptyList(),
                rawText = "Failed to dump appops (stderr: $stderr)"
            )
        }

        val snapshot = parser.parse(stdout, System.currentTimeMillis())
        Log.d(TAG, "Parsed ${snapshot.entries.size} entries from root output")
        return snapshot
    }

    override fun source(): EventSource = EventSource.ROOT

    companion object {
        private const val TAG = "RootEventSource"
    }
}
