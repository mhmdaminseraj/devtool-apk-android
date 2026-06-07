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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.components.StatusBadge
import com.example.viewmodel.DevTrackViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: DevTrackViewModel) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    var showAddTaskDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("تسک‌ها") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTaskDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "تسک جدید")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(projects) { project ->
                ProjectTasksSection(project, viewModel)
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (projects.isEmpty()) {
                item {
                    Text("هیچ پروژه‌ای وجود ندارد. ابتدا یک پروژه بسازید.", modifier = Modifier.padding(32.dp))
                }
            }
        }

        if (showAddTaskDialog) {
            AddTaskDialog(
                projects = projects,
                viewModel = viewModel,
                onDismiss = { showAddTaskDialog = false },
                onConfirm = { sectionId, title, priority, dueDate ->
                    viewModel.addTask(sectionId, title, priority, dueDate)
                    showAddTaskDialog = false
                }
            )
        }
    }
}

@Composable
fun ProjectTasksSection(project: Project, viewModel: DevTrackViewModel) {
    val tasks by viewModel.getTasksForProject(project.id).collectAsStateWithLifecycle(initialValue = emptyList())
    
    if (tasks.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(project.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                tasks.forEach { task ->
                    TaskItem(task, 
                        onToggle = { viewModel.toggleTaskCompletion(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    } else {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(project.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            Text("تسک فعالی ندارد.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                task.title,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
            if (task.dueDate != null) {
                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                Text("سررسید: ${dateFormat.format(Date(task.dueDate))}", style = MaterialTheme.typography.labelSmall)
            }
        }
        
        // Priority Badge
        val (statusText, isUrgent) = when {
            task.isCompleted -> "انجام شده" to false
            task.priority == TaskPriority.HIGH -> "فوری" to true
            task.dueDate != null && task.dueDate < System.currentTimeMillis() + 86400000 -> "در انتظار" to false // Pending/Soon
            else -> "آینده" to false // Future
        }
        StatusBadge(
            status = statusText,
            isComplete = task.isCompleted,
            isUrgent = isUrgent
        )
        
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "حذف تسک", tint = MaterialTheme.colorScheme.error)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    projects: List<Project>,
    viewModel: DevTrackViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String, String, TaskPriority, Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedProject by remember { mutableStateOf<Project?>(projects.firstOrNull()) }
    
    val pages by remember(selectedProject) { 
        selectedProject?.let { viewModel.getPagesForProject(it.id) } ?: kotlinx.coroutines.flow.flowOf(emptyList()) 
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    
    var selectedPage by remember(pages) { mutableStateOf<Page?>(pages.firstOrNull()) }
    
    val sections by remember(selectedPage) {
        selectedPage?.let { viewModel.getSectionsForPage(it.id) } ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    
    var selectedSection by remember(sections) { mutableStateOf<Section?>(sections.firstOrNull()) }
    
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    
    // UI State for expandable dropdowns
    var expandedProject by remember { mutableStateOf(false) }
    var expandedPage by remember { mutableStateOf(false) }
    var expandedSection by remember { mutableStateOf(false) }
    var expandedPriority by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ساخت تسک جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("نام تسک") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Project selection
                ExposedDropdownMenuBox(expanded = expandedProject, onExpandedChange = { expandedProject = !expandedProject }) {
                    OutlinedTextField(
                        value = selectedProject?.title ?: "پروژه...",
                        onValueChange = {}, readOnly = true, label = { Text("پروژه") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProject) }
                    )
                    ExposedDropdownMenu(expanded = expandedProject, onDismissRequest = { expandedProject = false }) {
                        projects.forEach { prj ->
                            DropdownMenuItem(text = { Text(prj.title) }, onClick = { selectedProject = prj; expandedProject = false })
                        }
                    }
                }
                
                // Page selection
                ExposedDropdownMenuBox(expanded = expandedPage, onExpandedChange = { expandedPage = !expandedPage }) {
                    OutlinedTextField(
                        value = selectedPage?.title ?: (if(pages.isEmpty()) "صفحه‌ای وجود ندارد" else "صفحه..."),
                        onValueChange = {}, readOnly = true, label = { Text("صفحه") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPage) }
                    )
                    ExposedDropdownMenu(expanded = expandedPage, onDismissRequest = { expandedPage = false }) {
                        pages.forEach { pg ->
                            DropdownMenuItem(text = { Text(pg.title) }, onClick = { selectedPage = pg; expandedPage = false })
                        }
                    }
                }

                // Section selection
                ExposedDropdownMenuBox(expanded = expandedSection, onExpandedChange = { expandedSection = !expandedSection }) {
                    OutlinedTextField(
                        value = selectedSection?.title ?: (if(sections.isEmpty()) "بخشی وجود ندارد" else "بخش/گروه..."),
                        onValueChange = {}, readOnly = true, label = { Text("بخش/گروه") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSection) }
                    )
                    ExposedDropdownMenu(expanded = expandedSection, onDismissRequest = { expandedSection = false }) {
                        sections.forEach { sec ->
                            DropdownMenuItem(text = { Text(sec.title) }, onClick = { selectedSection = sec; expandedSection = false })
                        }
                    }
                }
                
                // Priority selection
                ExposedDropdownMenuBox(expanded = expandedPriority, onExpandedChange = { expandedPriority = !expandedPriority }) {
                    OutlinedTextField(
                        value = when(priority) { TaskPriority.LOW -> "عادی"; TaskPriority.MEDIUM -> "مهم"; TaskPriority.HIGH -> "فوری" },
                        onValueChange = {}, readOnly = true, label = { Text("اولویت") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) }
                    )
                    ExposedDropdownMenu(expanded = expandedPriority, onDismissRequest = { expandedPriority = false }) {
                        DropdownMenuItem(text = { Text("عادی") }, onClick = { priority = TaskPriority.LOW; expandedPriority = false })
                        DropdownMenuItem(text = { Text("مهم") }, onClick = { priority = TaskPriority.MEDIUM; expandedPriority = false })
                        DropdownMenuItem(text = { Text("فوری") }, onClick = { priority = TaskPriority.HIGH; expandedPriority = false })
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (title.isNotBlank() && selectedSection != null) {
                        onConfirm(selectedSection!!.id, title, priority, null)
                    }
                },
                enabled = selectedSection != null
            ) { Text("تایید") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("لغو") }
        }
    )
}

