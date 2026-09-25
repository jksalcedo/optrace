package com.jksalcedo.optrace.ui.timeline

/**
 * Maps raw AppOps mode strings to user-friendly descriptions.
 *
 * Android's `appops` subsystem uses terse mode names (allow, ignore, error, foreground, default)
 * that can be confusing — e.g. "ignore" really means "silently denied" rather than "the system
 * is ignoring this permission". This helper provides opt-in human-readable translations.
 */
object ModeLabels {

    private val friendlyMap = mapOf(
        "allow" to "Allowed",
        "0" to "Allowed",
        "mode_allowed" to "Allowed",

        "ignore" to "Silently denied",
        "1" to "Silently denied",
        "mode_ignored" to "Silently denied",

        "deny" to "Denied",
        "3" to "Denied",
        "mode_denied" to "Denied",

        "foreground" to "Foreground only",
        "4" to "Foreground only",
        "mode_foreground" to "Foreground only",

        "errored" to "Errored (ask user)",
        "error" to "Errored (ask user)",
        "2" to "Errored (ask user)",
        "mode_errored" to "Errored (ask user)",

        "default" to "Default (system)",
        "mode_default" to "Default (system)",
    )

    /**
     * Returns a friendly label for the given raw mode, or the raw value itself if unknown.
     */
    fun friendly(rawMode: String): String =
        friendlyMap[rawMode.lowercase().trim()] ?: rawMode
}
