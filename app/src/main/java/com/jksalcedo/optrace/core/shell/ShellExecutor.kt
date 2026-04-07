package com.jksalcedo.optrace.core.shell

interface ShellExecutor {
    suspend fun execute(command: String): ShellResult
}

data class ShellResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String
)
