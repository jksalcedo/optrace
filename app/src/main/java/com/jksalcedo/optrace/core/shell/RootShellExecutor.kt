package com.jksalcedo.optrace.core.shell

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RootShellExecutor : ShellExecutor {

    override suspend fun execute(command: String): ShellResult = withContext(Dispatchers.IO) {
        runCatching {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val stdout = process.inputStream.bufferedReader().readText()
            val stderr = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            ShellResult(exitCode, stdout, stderr)
        }.getOrElse { e ->
            ShellResult(-1, "", e.message ?: "Unknown error")
        }
    }
}
