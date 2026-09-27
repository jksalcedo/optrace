package com.jksalcedo.optrace.ui.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences-backed user preferences.
 *
 * Note: the `friendlyModeLabels` preference was removed in the Privacy Dashboard
 * redesign (Phase 1). Mode labels are now always shown in human-readable form,
 * matching Android's Privacy Dashboard conventions. Raw values will be accessible
 * in a future Event Detail bottom sheet for power users.
 */
object UserPreferences {

    private const val PREFS_NAME = "optrace_settings"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
