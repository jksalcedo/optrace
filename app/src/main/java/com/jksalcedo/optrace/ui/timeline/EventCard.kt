package com.jksalcedo.optrace.ui.timeline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.NewLabel
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksalcedo.optrace.data.local.PermissionEventEntity
import com.jksalcedo.optrace.ui.theme.OpTraceTheme
import com.jksalcedo.optrace.utils.formatTimestamp

// ---------------------------------------------------------------------------
// EventCard — mimics the Android Privacy Dashboard chronological row
// ---------------------------------------------------------------------------

@Composable
fun EventCard(event: PermissionEventEntity) {
    val context = LocalContext.current
    val appInfo = remember(event.packageName) {
        AppInfoResolver.getAppInfo(context, event.packageName)
    }
    val group = remember(event.opName) { PermissionGroup.of(event.opName) }
    val label = remember(event.opName) { OpLabels.label(event.opName) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon
            AppIconImage(appInfo = appInfo)

            Spacer(Modifier.width(12.dp))

            // App name + package + relative time
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = appInfo.appName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = formatTimestamp(event.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = event.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(Modifier.height(6.dp))

                // Permission group row — icon + label + anomaly chip (if any)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PermissionGroupPill(group = group, label = label)

                    // Only surface an anomaly badge when something is NOT normal
                    val anomaly = anomalyForChangeType(event.changeType, event.mode)
                    if (anomaly != null) {
                        AnomalyBadge(anomaly)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// AppGroupCard — mimics the Android "App permissions" detail screen
// ---------------------------------------------------------------------------

@Composable
fun AppGroupCard(appGroup: AppGroupedEvents) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val appInfo = remember(appGroup.packageName) {
        AppInfoResolver.getAppInfo(context, appGroup.packageName)
    }

    // Collapse events to one row per permission group, keeping the most recent
    val groupedByPermission = remember(appGroup.events) {
        appGroup.events
            .groupBy { PermissionGroup.of(it.opName) }
            .mapValues { (_, events) -> events.maxByOrNull { it.timestamp }!! }
            .toSortedMap(compareBy { it.label })
    }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppIconImage(appInfo = appInfo)
                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appGroup.appName,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = appGroup.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    val count = groupedByPermission.size
                    Text(
                        text = "$count permission${if (count != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Expanded: one row per permission group
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

                    groupedByPermission.forEach { (group, latestEvent) ->
                        PermissionRow(group = group, latestEvent = latestEvent)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Shared sub-composables
// ---------------------------------------------------------------------------

/**
 * A coloured pill showing the permission group icon + label.
 * Matches Android's permission group chips in the Privacy Dashboard.
 */
@Composable
fun PermissionGroupPill(
    group: PermissionGroup,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = group.tint.copy(alpha = 0.12f),
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = group.icon,
                contentDescription = null,
                tint = group.tint,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = group.tint,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/**
 * A single permission row inside [AppGroupCard] — one row per permission group,
 * mirroring Android's "App permissions" detail screen layout.
 */
@Composable
private fun PermissionRow(
    group: PermissionGroup,
    latestEvent: PermissionEventEntity,
) {
    val label = OpLabels.label(latestEvent.opName)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = group.icon,
                contentDescription = null,
                tint = group.tint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            // Show mode only when it is NOT the normal "allow" case
            val rawMode = latestEvent.mode
            if (rawMode != null && rawMode.lowercase() !in setOf("allow", "0", "mode_allowed")) {
                Text(
                    text = ModeLabels.friendly(rawMode),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Text(
            text = formatTimestamp(latestEvent.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// Anomaly helpers — only surface non-normal events
// ---------------------------------------------------------------------------

private data class Anomaly(val label: String, val icon: ImageVector, val color: Color)

private fun anomalyForChangeType(changeType: String, mode: String?): Anomaly? = when (changeType) {
    "LAST_REJECT_INCREASED" -> Anomaly("Denied",      Icons.Outlined.Block,    Color(0xFFF44336))
    "MODE_CHANGED"          -> Anomaly("Mode changed", Icons.Outlined.Warning,  Color(0xFFFF9800))
    "NEW_OP"                -> Anomaly("New",          Icons.Outlined.NewLabel, Color(0xFF4CAF50))
    // LAST_ACCESS_INCREASED is the normal case — no badge
    // ATTRIBUTION_CHANGED is low-signal — suppress
    else                    -> null
}

@Composable
private fun AnomalyBadge(anomaly: Anomaly) {
    Surface(
        color = anomaly.color.copy(alpha = 0.12f),
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = anomaly.icon,
                contentDescription = null,
                tint = anomaly.color,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = anomaly.label,
                style = MaterialTheme.typography.labelSmall,
                color = anomaly.color,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun EventCardPreview() {
    OpTraceTheme {
        EventCard(
            event = PermissionEventEntity(
                timestamp = System.currentTimeMillis() - 120_000,
                capturedAt = System.currentTimeMillis(),
                snapshotId = "preview",
                parserVersion = "v1",
                uid = 10000,
                packageName = "com.example.app",
                opName = "android:camera",
                mode = "allow",
                attributionTag = null,
                changeType = "LAST_ACCESS_INCREASED",
                source = "ROOT",
                confidence = "DIRECT",
                rawRef = null
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppGroupCardPreview() {
    OpTraceTheme {
        AppGroupCard(
            appGroup = AppGroupedEvents(
                packageName = "com.example.app",
                appName = "Example App",
                events = listOf(
                    PermissionEventEntity(
                        timestamp = System.currentTimeMillis() - 120_000,
                        capturedAt = System.currentTimeMillis(),
                        snapshotId = "preview",
                        parserVersion = "v1",
                        uid = 10000,
                        packageName = "com.example.app",
                        opName = "android:camera",
                        mode = "allow",
                        attributionTag = null,
                        changeType = "LAST_ACCESS_INCREASED",
                        source = "ROOT",
                        confidence = "DIRECT",
                        rawRef = null
                    ),
                    PermissionEventEntity(
                        timestamp = System.currentTimeMillis() - 600_000,
                        capturedAt = System.currentTimeMillis(),
                        snapshotId = "preview",
                        parserVersion = "v1",
                        uid = 10000,
                        packageName = "com.example.app",
                        opName = "android:fine_location",
                        mode = "ignore",
                        attributionTag = null,
                        changeType = "MODE_CHANGED",
                        source = "ROOT",
                        confidence = "DIRECT",
                        rawRef = null
                    )
                )
            )
        )
    }
}