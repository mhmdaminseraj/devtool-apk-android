package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("زمان‌بندی پروژه‌ها") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text("نقشه راه و زمان‌بندی", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("در نسخه‌های بعدی این قابلیت اضافه خواهد شد.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
