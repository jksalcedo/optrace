package com.jksalcedo.optrace.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jksalcedo.optrace.domain.usecase.CollectPermissionEventsUseCase

class CollectWorker(
    appContext: Context,
    params: WorkerParameters,
    private val collect: CollectPermissionEventsUseCase
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = runCatching {
        collect()
        Result.success()
    }.getOrElse { Result.retry() }
}
