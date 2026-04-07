package com.jksalcedo.optrace.core.capability

import com.jksalcedo.optrace.domain.model.CapabilityTier
import rikka.shizuku.Shizuku
import java.io.File

class CapabilityResolverImpl : CapabilityResolver {
    override suspend fun resolve(): CapabilityState {
        val shizukuAvailable = runCatching { Shizuku.pingBinder() }.getOrDefault(false)
        val rootAvailable = isRootAvailable()

        val tier = when {
            rootAvailable -> CapabilityTier.ROOT
            shizukuAvailable -> CapabilityTier.SHIZUKU
            else -> CapabilityTier.FALLBACK
        }

        return CapabilityState(
            tier = tier,
            shizukuAvailable = shizukuAvailable,
            rootAvailable = rootAvailable
        )
    }

    private fun isRootAvailable(): Boolean {
        val paths = System.getenv("PATH")?.split(":") ?: emptyList()
        return paths.any { File(it, "su").exists() }
    }
}
