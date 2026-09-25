package com.jksalcedo.optrace.ui.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simple SharedPreferences-backed user preferences.
 * Provides reactive [StateFlow]s so Compose can recompose on changes.
 */
object UserPreferences {

    private const val PREFS_NAME = "optrace_settings"
    private const val KEY_FRIENDLY_MODES = "friendly_mode_labels"

    private lateinit var prefs: SharedPreferences

    private val _friendlyModeLabels = MutableStateFlow(false)
    val friendlyModeLabels: StateFlow<Boolean> = _friendlyModeLabels.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _friendlyModeLabels.value = prefs.getBoolean(KEY_FRIENDLY_MODES, false)
    }

    fun setFriendlyModeLabels(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FRIENDLY_MODES, enabled).apply()
        _friendlyModeLabels.value = enabled
    }
}
