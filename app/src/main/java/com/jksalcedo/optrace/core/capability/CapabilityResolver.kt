package com.jksalcedo.optrace.core.capability

interface CapabilityResolver {
    suspend fun resolve(): CapabilityState
}
