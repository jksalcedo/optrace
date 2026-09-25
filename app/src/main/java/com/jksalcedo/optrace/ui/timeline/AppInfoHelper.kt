package com.jksalcedo.optrace.ui.timeline

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

import java.util.concurrent.ConcurrentHashMap
import java.util.Optional

data class AppInfo(
    val appName: String,
    val iconBitmap: ImageBitmap?
)

object AppInfoResolver {
    private val nameCache = ConcurrentHashMap<String, String>()
    // ConcurrentHashMap does not support null values, so we wrap it in Optional
    private val iconCache = ConcurrentHashMap<String, Optional<ImageBitmap>>()

    fun getAppName(context: Context, rawPackageName: String): String {
        return nameCache.getOrPut(rawPackageName) {
            try {
                val resolvedPkg = resolvePackage(context, rawPackageName)
                val pm = context.packageManager
                val appInfo = pm.getApplicationInfo(resolvedPkg, 0)
                pm.getApplicationLabel(appInfo).toString()
            } catch (_: Exception) {
                resolvePackage(context, rawPackageName)
            }
        }
    }

    fun getAppIcon(context: Context, rawPackageName: String): ImageBitmap? {
        val cached = iconCache[rawPackageName]
        if (cached != null) return cached.orElse(null)

        val bmp = try {
            val resolvedPkg = resolvePackage(context, rawPackageName)
            val pm = context.packageManager
            val drawable = pm.getApplicationIcon(resolvedPkg)
            drawable.toBitmap(width = 96, height = 96).asImageBitmap()
        } catch (_: Exception) {
            null
        }
        iconCache[rawPackageName] = Optional.ofNullable(bmp)
        return bmp
    }

    fun getAppInfo(context: Context, rawPackageName: String): AppInfo {
        return AppInfo(
            appName = getAppName(context, rawPackageName),
            iconBitmap = getAppIcon(context, rawPackageName)
        )
    }

    private fun resolvePackage(context: Context, rawPackageName: String): String {
        val cleanPkg = rawPackageName
            .removePrefix("uid:")
            .removeSuffix(":")
            .trim()

        return if (cleanPkg.all { it.isDigit() }) {
            val uid = cleanPkg.toIntOrNull()
            if (uid != null) {
                context.packageManager.getPackagesForUid(uid)?.firstOrNull() ?: cleanPkg
            } else cleanPkg
        } else {
            cleanPkg
        }
    }
}
