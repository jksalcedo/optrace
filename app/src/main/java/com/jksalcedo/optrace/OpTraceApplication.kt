package com.jksalcedo.optrace

import android.app.Application
import androidx.room.Room
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jksalcedo.optrace.core.capability.CapabilityResolverImpl
import com.jksalcedo.optrace.core.parser.AppOpsParserV1
import com.jksalcedo.optrace.core.shell.RootShellExecutor
import com.jksalcedo.optrace.data.local.AppDatabase
import com.jksalcedo.optrace.data.local.SnapshotFileStorage
import com.jksalcedo.optrace.domain.source.FallbackEventSource
import com.jksalcedo.optrace.domain.source.RootEventSource
import com.jksalcedo.optrace.domain.source.ShizukuEventSource
import com.jksalcedo.optrace.domain.usecase.CollectPermissionEventsUseCase
import com.jksalcedo.optrace.domain.usecase.DiffSnapshotsUseCase
import com.jksalcedo.optrace.worker.CollectWorker
import com.jksalcedo.optrace.worker.CollectWorkerFactory
import java.util.concurrent.TimeUnit

class OpTraceApplication : Application(), Configuration.Provider {

    lateinit var database: AppDatabase
        private set

    lateinit var collectUseCase: CollectPermissionEventsUseCase
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(this, AppDatabase::class.java, "optrace.db")
            .fallbackToDestructiveMigration()
            .build()

        val parser = AppOpsParserV1()
        val rootShell = RootShellExecutor()

        val rootSource = RootEventSource(rootShell, parser)
        val shizukuSource = ShizukuEventSource(this)
        val fallbackSource = FallbackEventSource(this)

        collectUseCase = CollectPermissionEventsUseCase(
            resolver = CapabilityResolverImpl(),
            root = rootSource,
            shizuku = shizukuSource,
            fallback = fallbackSource,
            differ = DiffSnapshotsUseCase(),
            eventDao = database.permissionEventDao(),
            snapshotStorage = SnapshotFileStorage(this),
            parser = parser
        )

        schedulePeriodicCollection()
    }

    private fun schedulePeriodicCollection() {
        val collectWorkRequest = PeriodicWorkRequestBuilder<CollectWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            collectWorkRequest
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(CollectWorkerFactory(collectUseCase))
            .build()

    companion object {
        const val WORK_NAME = "optrace_periodic_collection"
    }
}
