package com.jksalcedo.optrace.ui.status

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jksalcedo.optrace.OpTraceApplication
import com.jksalcedo.optrace.core.capability.CapabilityResolverImpl
import com.jksalcedo.optrace.core.capability.CapabilityState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(resolver: CapabilityResolverImpl) {
    var state by remember { mutableStateOf<CapabilityState?>(null) }
    var isAuditing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext as OpTraceApplication

    LaunchedEffect(Unit) {
        state = resolver.resolve()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Status") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
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

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        isAuditing = true
                        try {
                            context.collectUseCase()
                        } catch (_: Exception) {
                        } finally {
                            isAuditing = false
                        }
                    }
                },
                enabled = !isAuditing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isAuditing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Auditing...")
                } else {
                    Text("Run Audit Now")
                }
            }
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String) {
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
