package com.jksalcedo.optrace.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.jksalcedo.optrace.domain.usecase.CollectPermissionEventsUseCase

class CollectWorkerFactory(
    private val collect: CollectPermissionEventsUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            CollectWorker::class.java.name -> CollectWorker(appContext, workerParameters, collect)
            else -> null
        }
    }
}
