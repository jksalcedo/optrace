package com.jksalcedo.optrace.ui.timeline

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(viewModel: TimelineViewModel = viewModel()) {
    val filteredEvents by viewModel.filteredEvents.collectAsState()
    val dateGroupedEvents by viewModel.dateGroupedEvents.collectAsState()
    val appGroupedEvents by viewModel.appGroupedEvents.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val showOnlyAccesses by viewModel.showOnlyAccesses.collectAsState()
    val groupingMode by viewModel.groupingMode.collectAsState()

    var isSearchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchExpanded) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchQuery.value = it },
                            placeholder = { Text("Search apps or permissions...") },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (searchQuery.isNotEmpty()) {
                                        viewModel.searchQuery.value = ""
                                    } else {
                                        isSearchExpanded = false
                                    }
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Close search")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("Permission Timeline", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    if (!isSearchExpanded) {
                        IconButton(onClick = { isSearchExpanded = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Controls & Category Filters
            TimelineControlHeader(
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectedCategory.value = it },
                showOnlyAccesses = showOnlyAccesses,
                onOnlyAccessesChanged = { viewModel.showOnlyAccesses.value = it },
                groupingMode = groupingMode,
                onGroupingModeChanged = { viewModel.groupingMode.value = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            if (filteredEvents.isEmpty() && !isRefreshing) {
                EmptyState(
                    onScanClick = { viewModel.refresh() },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (groupingMode == GroupingMode.BY_DATE) {
                        dateGroupedEvents.forEach { group ->
                            item(key = "header_${group.dateHeader}") {
                                DateHeader(group.dateHeader, group.events.size)
                            }
                            items(group.events, key = { it.id }) { event ->
                                EventCard(event)
                            }
                        }
                    } else {
                        items(appGroupedEvents, key = { it.packageName }) { appGroup ->
                            AppGroupCard(appGroup)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineControlHeader(
    selectedCategory: PermissionCategory,
    onCategorySelected: (PermissionCategory) -> Unit,
    showOnlyAccesses: Boolean,
    onOnlyAccessesChanged: (Boolean) -> Unit,
    groupingMode: GroupingMode,
    onGroupingModeChanged: (GroupingMode) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PermissionCategory.entries.forEach { category ->
                val group = when (category) {
                    PermissionCategory.CAMERA     -> PermissionGroup.CAMERA
                    PermissionCategory.LOCATION   -> PermissionGroup.LOCATION
                    PermissionCategory.MICROPHONE -> PermissionGroup.MICROPHONE
                    PermissionCategory.CONTACTS   -> PermissionGroup.CONTACTS
                    PermissionCategory.STORAGE    -> PermissionGroup.STORAGE
                    PermissionCategory.OTHER,
                    PermissionCategory.ALL        -> null
                }
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category.label) },
                    leadingIcon = if (group != null) ({
                        Icon(
                            imageVector = group.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedCategory == category)
                                group.tint
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }) else null
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Options: Grouping & Accesses Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = showOnlyAccesses,
                    onClick = { onOnlyAccessesChanged(!showOnlyAccesses) },
                    label = { Text(if (showOnlyAccesses) "Accesses Only" else "All Events") },
                    leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            // Segmented Grouping Mode Selector
            SingleChoiceSegmentedButtonRow {
                GroupingMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = groupingMode == mode,
                        onClick = { onGroupingModeChanged(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = GroupingMode.entries.size)
                    ) {
                        Text(mode.label, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DateHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "$count event${if (count > 1) "s" else ""}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AppIconImage(appInfo: AppInfo) {
    if (appInfo.iconBitmap != null) {
        Image(
            bitmap = appInfo.iconBitmap,
            contentDescription = appInfo.appName,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
    } else {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = appInfo.appName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// ChangeTypeBadge removed — replaced by AnomalyBadge in EventCard.kt
// which only surfaces non-normal events (deny / mode-change / new op).

@Composable
private fun EmptyState(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "No events recorded",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Tap below to scan AppOps permissions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onScanClick) {
                Text("Scan Now")
            }
        }
    }
}
