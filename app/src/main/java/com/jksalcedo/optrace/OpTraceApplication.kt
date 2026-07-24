package com.jksalcedo.optrace

import android.app.Application
import androidx.room.Room
import androidx.work.Configuration
import com.jksalcedo.optrace.core.capability.CapabilityResolverImpl
import com.jksalcedo.optrace.core.parser.AppOpsParserV1
import com.jksalcedo.optrace.core.shell.RootShellExecutor
import com.jksalcedo.optrace.data.local.AppDatabase
import com.jksalcedo.optrace.domain.source.FallbackEventSource
import com.jksalcedo.optrace.domain.source.RootEventSource
import com.jksalcedo.optrace.domain.source.ShizukuEventSource
import com.jksalcedo.optrace.domain.usecase.CollectPermissionEventsUseCase
import com.jksalcedo.optrace.domain.usecase.DiffSnapshotsUseCase
import com.jksalcedo.optrace.worker.CollectWorkerFactory

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
            snapshotDao = database.snapshotCacheDao(),
            parser = parser
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(CollectWorkerFactory(collectUseCase))
            .build()
}
