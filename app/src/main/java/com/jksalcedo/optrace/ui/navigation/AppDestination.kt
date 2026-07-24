package com.jksalcedo.optrace.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val label: String,
    val icon: ImageVector
) {
    TIMELINE("Timeline", Icons.Default.List),
    STATUS("Status", Icons.Default.Info)
}
