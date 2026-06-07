package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Project
import com.example.ui.components.AnimatedProgressBar
import com.example.viewmodel.DevTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: DevTrackViewModel,
    onProjectClick: (String) -> Unit,
    onNewProject: () -> Unit
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("پروژه‌ها") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewProject) {
                Icon(Icons.Default.Add, contentDescription = "پروژه جدید")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(projects) { project ->
                ProjectCard(project = project, viewModel = viewModel, onClick = { onProjectClick(project.id) })
            }
            if (projects.isEmpty()) {
                item {
                    Text("هیچ پروژه‌ای وجود ندارد. روی دکمه + کلیک کنید.", modifier = Modifier.padding(32.dp))
                }
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, viewModel: DevTrackViewModel, onClick: () -> Unit) {
    // For automatic calculation, we fetch tasks for this project
    val tasks by viewModel.getTasksForProject(project.id).collectAsStateWithLifecycle(initialValue = emptyList())
    val progress = if (tasks.isEmpty()) 0f else tasks.count { it.isCompleted }.toFloat() / tasks.size
    
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (project.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(project.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف پروژه", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("پیشرفت", style = MaterialTheme.typography.labelMedium)
                Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            AnimatedProgressBar(progress = progress)
        }
    }
    
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("حذف پروژه") },
            text = { Text("آیا از حذف پروژه «${project.title}» و تمام صفحات، بخش‌ها و تسک‌های آن مطمئن هستید؟ این عمل غیرقابل بازگشت است.") },
            confirmButton = {
                Button(onClick = { 
                    viewModel.deleteProject(project)
                    showDeleteConfirm = false 
                }) { Text("بله، حذف کن") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("لغو") }
            }
        )
    }
}
