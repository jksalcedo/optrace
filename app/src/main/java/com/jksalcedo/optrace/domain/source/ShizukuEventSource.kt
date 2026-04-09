package com.jksalcedo.optrace.domain.source

 import android.content.Context
 import com.jksalcedo.optrace.domain.model.AppOpsSnapshot
 import com.jksalcedo.optrace.domain.model.EventSource

class ShizukuEventSource(private val context: Context) : PermissionEventSource {
         override suspend fun captureSnapshot(): AppOpsSnapshot {
                return AppOpsSnapshot(
                    capturedAt = TODO(),
                    entries = TODO()
                )
           }

         override fun source(): EventSource = EventSource.SHIZUKU
     }
