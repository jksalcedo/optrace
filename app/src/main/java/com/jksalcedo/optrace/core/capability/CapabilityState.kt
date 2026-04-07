package com.jksalcedo.optrace.core.capability

import com.jksalcedo.optrace.domain.model.CapabilityTier

data class CapabilityState(
    val tier: CapabilityTier,
    val shizukuAvailable: Boolean,
    val rootAvailable: Boolean,
    val reason: String? = null
)
