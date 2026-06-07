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

import com.example.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DevTrackViewModel,
    onProjectClick: (String) -> Unit
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val recentTasks by viewModel.recentTasks.collectAsStateWithLifecycle()
    val spacing = MaterialTheme.spacing

    Scaffold(
        topBar = { TopAppBar(title = { Text("داشبورد") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = spacing.large),
            contentPadding = PaddingValues(vertical = spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.extraLarge)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.large)) {
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
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(spacing.large)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(spacing.small))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
