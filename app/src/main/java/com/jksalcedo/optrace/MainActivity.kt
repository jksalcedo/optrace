package com.jksalcedo.optrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jksalcedo.optrace.core.capability.CapabilityResolverImpl
import com.jksalcedo.optrace.core.capability.CapabilityState
import com.jksalcedo.optrace.domain.model.CapabilityTier
import com.jksalcedo.optrace.ui.theme.OpTraceTheme
import kotlinx.coroutines.launch

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
                    StatusScreen(capabilityResolver)
                }
            }
        }
    }
}

@Composable
fun StatusScreen(resolver: CapabilityResolverImpl) {
    var state by remember { mutableStateOf<CapabilityState?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        state = resolver.resolve()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("OpTrace Status", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        state?.let { s ->
            StatusRow("Capability Tier", s.tier.name)
            StatusRow("Shizuku Available", s.shizukuAvailable.toString())
            StatusRow("Root Available", s.rootAvailable.toString())
        } ?: Text("Loading status...")

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {
                scope.launch {
                    state = resolver.resolve()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh Capability")
        }
    }
}

@Composable
fun StatusRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}
