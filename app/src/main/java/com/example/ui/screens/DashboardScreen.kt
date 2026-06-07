package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.DevTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DevTrackViewModel,
    onProjectClick: (String) -> Unit
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val recentTasks by viewModel.recentTasks.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("داشبورد") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(title = "پروژه‌ها", value = projects.size.toString(), modifier = Modifier.weight(1f))
                    StatCard(title = "تسک‌های اخیر", value = recentTasks.size.toString(), modifier = Modifier.weight(1f))
                }
            }

            item {
                Text("پروژه‌های اخیر", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            items(projects.take(3)) { project ->
                ProjectCard(project = project, viewModel = viewModel, onClick = { onProjectClick(project.id) })
            }
            if (projects.isEmpty()) {
                item { Text("بدون پروژه") }
            }

            item {
                Text("تسک‌های باز", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            items(recentTasks.filter { !it.isCompleted }.take(5)) { task ->
                TaskItem(
                    task = task, 
                    onToggle = { viewModel.toggleTaskCompletion(task) },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }
            if (recentTasks.isEmpty()) {
                item { Text("بدون تسک فعال") }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}
