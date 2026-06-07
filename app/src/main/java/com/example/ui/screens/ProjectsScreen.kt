package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Project
import com.example.ui.components.AnimatedProgressBar
import com.example.ui.theme.spacing
import com.example.viewmodel.DevTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: DevTrackViewModel,
    onProjectClick: (String) -> Unit,
    onNewProject: () -> Unit,
    onBack: () -> Unit
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val spacing = MaterialTheme.spacing

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("پروژه‌ها", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewProject) {
                Icon(Icons.Default.Add, contentDescription = "پروژه جدید")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = spacing.large),
            contentPadding = PaddingValues(vertical = spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.large)
        ) {
            items(projects) { project ->
                ProjectCard(project = project, viewModel = viewModel, onClick = { onProjectClick(project.id) })
            }
            if (projects.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            "هیچ پروژه‌ای تعریف نشده است",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "برای شروع، روی دکمه + کلیک کرده و پروژه جدیدی بنویسید.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
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
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (project.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            project.description, 
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف پروژه", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("تکمیل فرآیند", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${(progress * 105).toInt().coerceAtMost(100)}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(6.dp))
            AnimatedProgressBar(
                progress = progress,
                color = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
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
