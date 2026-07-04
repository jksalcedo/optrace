package com.jksalcedo.optrace.appops

import android.app.AppOpsManager
import android.content.Context

class AppOpsStarter(
    private val context: Context
) {
    private val appOps = context.getSystemService(AppOpsManager::class.java)

    fun noteSelfCameraAttempt() {
        // Example: records/checks an op for *your own app* context.
        // On newer Android versions prefer noteOpNoThrow/checkOpNoThrow variants as available.
        val op = AppOpsManager.OPSTR_CAMERA
        val uid = android.os.Process.myUid()
        val pkg = context.packageName

        // API surface varies by Android version; keep version guards.
        @Suppress("DEPRECATION")
        val mode = appOps.checkOpNoThrow(op, uid, pkg)
        // mode: MODE_ALLOWED / MODE_IGNORED / MODE_ERRORED / MODE_DEFAULT / ...
        android.util.Log.d("AppOpsStarter", "Self op=$op mode=$mode")
    }

    fun startWatchingSelfCamera(onChanged: (String, String) -> Unit) {
        val op = AppOpsManager.OPSTR_CAMERA
        val listener = AppOpsManager.OnOpChangedListener { changedOp, packageName ->
            onChanged(changedOp, packageName)
        }
        // Watching all packages is restricted on many versions; start with self package expectations.
        appOps.startWatchingMode(op, context.packageName, listener)
    }
}