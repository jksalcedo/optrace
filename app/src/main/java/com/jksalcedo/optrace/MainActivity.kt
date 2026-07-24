package com.jksalcedo.optrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jksalcedo.optrace.core.capability.CapabilityResolverImpl
import com.jksalcedo.optrace.ui.navigation.AppDestination
import com.jksalcedo.optrace.ui.status.StatusScreen
import com.jksalcedo.optrace.ui.theme.OpTraceTheme
import com.jksalcedo.optrace.ui.timeline.TimelineScreen

class MainActivity : ComponentActivity() {
    private val capabilityResolver = CapabilityResolverImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpTraceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(capabilityResolver)
                }
            }
        }
    }
}

@Composable
private fun AppNavigation(resolver: CapabilityResolverImpl) {
    var currentDestination by remember { mutableStateOf(AppDestination.TIMELINE) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestination.entries.forEach { dest ->
                item(
                    selected = currentDestination == dest,
                    onClick = { currentDestination = dest },
                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                    label = { Text(dest.label) }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestination.TIMELINE -> TimelineScreen()
            AppDestination.STATUS -> StatusScreen(resolver)
        }
    }
}
