package com.jksalcedo.optrace.domain.usecase

import com.jksalcedo.optrace.core.capability.CapabilityResolver
import com.jksalcedo.optrace.domain.model.CapabilityTier
import com.jksalcedo.optrace.domain.source.PermissionEventSource

class CollectPermissionEventsUseCase(
    private val resolver: CapabilityResolver,
    private val root: PermissionEventSource,
    private val shizuku: PermissionEventSource,
    private val fallback: PermissionEventSource,
    // repos...
) {
    suspend operator fun invoke() {
        val cap = resolver.resolve()
        val src = when (cap.tier) {
            CapabilityTier.ROOT -> root
            CapabilityTier.SHIZUKU -> shizuku
            CapabilityTier.FALLBACK -> fallback
        }

        val current = src.captureSnapshot()
        // load previous snapshot
        // diff
        // persist normalized events + current snapshot
    }
}
