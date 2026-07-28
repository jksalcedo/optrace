package com.jksalcedo.optrace.ui.timeline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jksalcedo.optrace.OpTraceApplication
import com.jksalcedo.optrace.data.local.PermissionEventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PermissionCategory(val label: String) {
    ALL("All"),
    CAMERA("Camera"),
    LOCATION("Location"),
    MICROPHONE("Microphone"),
    CONTACTS("Contacts"),
    STORAGE("Storage"),
    OTHER("Other")
}

enum class GroupingMode(val label: String) {
    BY_DATE("By Date"),
    BY_APP("By App")
}

data class DateGroupedEvents(
    val dateHeader: String,
    val events: List<PermissionEventEntity>
)

data class AppGroupedEvents(
    val packageName: String,
    val appName: String,
    val events: List<PermissionEventEntity>
)

class TimelineViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as OpTraceApplication
    private val dao = app.database.permissionEventDao()

    private val _rawEvents = dao.observeLatest(500)

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow(PermissionCategory.ALL)
    val showOnlyAccesses = MutableStateFlow(false)
    val groupingMode = MutableStateFlow(GroupingMode.BY_DATE)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val filteredEvents: StateFlow<List<PermissionEventEntity>> = combine(
        _rawEvents,
        searchQuery,
        selectedCategory,
        showOnlyAccesses
    ) { events, query, category, onlyAccesses ->
        events.filter { event ->
            // Category filter
            val matchesCategory = when (category) {
                PermissionCategory.ALL -> true
                PermissionCategory.CAMERA -> event.opName.contains("camera", ignoreCase = true)
                PermissionCategory.LOCATION -> event.opName.contains("location", ignoreCase = true)
                PermissionCategory.MICROPHONE -> event.opName.contains("audio", ignoreCase = true) || event.opName.contains("record", ignoreCase = true)
                PermissionCategory.CONTACTS -> event.opName.contains("contacts", ignoreCase = true)
                PermissionCategory.STORAGE -> event.opName.contains("storage", ignoreCase = true)
                PermissionCategory.OTHER -> !listOf("camera", "location", "audio", "record", "contacts", "storage").any { event.opName.contains(it, ignoreCase = true) }
            }

            // Access filter: strictly matches LAST_ACCESS_INCREASED or LAST_REJECT_INCREASED
            val matchesAccess = if (onlyAccesses) {
                event.changeType == "LAST_ACCESS_INCREASED" || event.changeType == "LAST_REJECT_INCREASED"
            } else {
                true
            }

            // Search query filter
            val appInfo = AppInfoResolver.getAppInfo(app, event.packageName)
            val matchesQuery = query.isBlank() ||
                    event.packageName.contains(query, ignoreCase = true) ||
                    appInfo.appName.contains(query, ignoreCase = true) ||
                    event.opName.contains(query, ignoreCase = true)

            matchesCategory && matchesAccess && matchesQuery
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dateGroupedEvents: StateFlow<List<DateGroupedEvents>> = filteredEvents.combine(groupingMode) { events, mode ->
        if (mode != GroupingMode.BY_DATE) return@combine emptyList()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())

        val now = System.currentTimeMillis()
        val todayStr = dateFormat.format(Date(now))
        val yesterdayStr = dateFormat.format(Date(now - 86_400_000))

        events.groupBy { event ->
            val eventDateStr = dateFormat.format(Date(event.timestamp))
            when (eventDateStr) {
                todayStr -> "Today"
                yesterdayStr -> "Yesterday"
                else -> displayFormat.format(Date(event.timestamp))
            }
        }.map { (header, items) ->
            DateGroupedEvents(header, items)
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val appGroupedEvents: StateFlow<List<AppGroupedEvents>> = filteredEvents.combine(groupingMode) { events, mode ->
        if (mode != GroupingMode.BY_APP) return@combine emptyList()

        events.groupBy { it.packageName }
            .map { (pkg, items) ->
                val appInfo = AppInfoResolver.getAppInfo(app, pkg)
                AppGroupedEvents(pkg, appInfo.appName, items)
            }
            .sortedBy { it.appName.lowercase() }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            try {
                app.collectUseCase()
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
