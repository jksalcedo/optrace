package com.jksalcedo.optrace.ui.timeline

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

data class AppInfo(
    val appName: String,
    val iconBitmap: ImageBitmap?
)

object AppInfoResolver {
    private val nameCache = mutableMapOf<String, String>()
    private val iconCache = mutableMapOf<String, ImageBitmap?>()

    fun getAppInfo(context: Context, rawPackageName: String): AppInfo {
        val cleanPkg = rawPackageName
            .removePrefix("uid:")
            .removeSuffix(":")
            .trim()

        val resolvedPkg = if (cleanPkg.all { it.isDigit() }) {
            val uid = cleanPkg.toIntOrNull()
            if (uid != null) {
                context.packageManager.getPackagesForUid(uid)?.firstOrNull() ?: cleanPkg
            } else cleanPkg
        } else {
            cleanPkg
        }

        val name = nameCache.getOrPut(rawPackageName) {
            try {
                val pm = context.packageManager
                val appInfo = pm.getApplicationInfo(resolvedPkg, 0)
                pm.getApplicationLabel(appInfo).toString()
            } catch (_: Exception) {
                resolvedPkg
            }
        }

        val icon = iconCache.getOrPut(rawPackageName) {
            try {
                val pm = context.packageManager
                val drawable = pm.getApplicationIcon(resolvedPkg)
                drawable.toBitmap(width = 96, height = 96).asImageBitmap()
            } catch (_: Exception) {
                null
            }
        }

        return AppInfo(appName = name, iconBitmap = icon)
    }
}
