package com.jksalcedo.optrace.ui.timeline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jksalcedo.optrace.OpTraceApplication
import com.jksalcedo.optrace.data.local.PermissionEventEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TimelineViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = (application as OpTraceApplication).database.permissionEventDao()

    val events: StateFlow<List<PermissionEventEntity>> = dao.observeLatest(500)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
