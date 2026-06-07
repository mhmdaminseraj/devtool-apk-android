package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Page
import com.example.data.PageType
import com.example.data.Section
import com.example.data.Task
import com.example.data.TaskPriority
import com.example.viewmodel.DevTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteStructureScreen(
    projectId: String,
    viewModel: DevTrackViewModel,
    onBack: () -> Unit
) {
    val pages by viewModel.getPagesForProject(projectId).collectAsStateWithLifecycle(initialValue = emptyList())
    val allTasks by viewModel.getTasksForProject(projectId).collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddPageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ساختار سایت") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddPageDialog = true },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("افزودن صفحه") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pages, key = { it.id }) { page ->
                PageItem(page = page, viewModel = viewModel, allTasks = allTasks)
            }
            if (pages.isEmpty()) {
                item {
                    Text(
                        "هنوز صفحه‌ای افزوده نشده. برای شروع، روی دکمه افزودن کلیک کنید.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                    )
                }
            }
        }

        if (showAddPageDialog) {
            AddPageDialog(
                onDismiss = { showAddPageDialog = false },
                onConfirm = { title, type ->
                    viewModel.addPage(projectId, title, type)
                    showAddPageDialog = false
                }
            )
        }
    }
}

@Composable
fun PageItem(page: Page, viewModel: DevTrackViewModel, allTasks: List<Task>) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val sections by viewModel.getSectionsForPage(page.id).collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddSectionDialog by remember { mutableStateOf(false) }

    // Reactive calculations for overall feedback and progress bar
    val pageTasks = remember(allTasks, sections) {
        allTasks.filter { task -> sections.any { section -> section.id == task.sectionId } }
    }
    val totalPageTasks = pageTasks.size
    val completedPageTasks = pageTasks.count { it.isCompleted }
    val pageProgress = if (totalPageTasks == 0) 0f else completedPageTasks.toFloat() / totalPageTasks

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(page.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            getPageTypeName(page.type), 
                            style = MaterialTheme.typography.bodySmall, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (totalPageTasks > 0) {
                            Text(
                                "($completedPageTasks از $totalPageTasks تسک)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                IconButton(onClick = { viewModel.deletePage(page) }) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف صفحه", tint = MaterialTheme.colorScheme.error)
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle"
                )
            }

            // Always visible page level progress bar
            if (totalPageTasks > 0) {
                com.example.ui.components.AnimatedProgressBar(
                    progress = pageProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    backgroundColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            }

            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(bottom = 8.dp)
                ) {
                    sections.forEach { section ->
                        val sectionTasks = remember(allTasks) {
                            allTasks.filter { it.sectionId == section.id }
                        }
                        val totalSectionTasks = sectionTasks.size
                        val completedSectionTasks = sectionTasks.count { it.isCompleted }
                        val sectionProgress = if (totalSectionTasks == 0) 0f else completedSectionTasks.toFloat() / totalSectionTasks
                        
                        var showSectionTaskAddDialog by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(section.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        if (section.isCategory) {
                                            Text(
                                                "دسته‌بندی", 
                                                style = MaterialTheme.typography.labelSmall, 
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    if (totalSectionTasks > 0) {
                                        Text(
                                            "پیشرفت: $completedSectionTasks از $totalSectionTasks (${(sectionProgress * 105).toInt().coerceAtMost(100)}%)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        Text(
                                            "تسک فعالی تعریف نشده",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { showSectionTaskAddDialog = true }) {
                                        Icon(Icons.Default.Add, contentDescription = "افزودن تسک", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.deleteSection(section) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "حذف بخش", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }

                            if (totalSectionTasks > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                com.example.ui.components.AnimatedProgressBar(
                                    progress = sectionProgress,
                                    modifier = Modifier.fillMaxWidth().height(4.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    backgroundColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            }

                            if (sectionTasks.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    sectionTasks.forEach { task ->
                                        var showTaskEditDialog by remember { mutableStateOf(false) }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Checkbox(
                                                    checked = task.isCompleted,
                                                    onCheckedChange = { viewModel.toggleTaskCompletion(task) }
                                                )
                                                
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        task.title,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Medium,
                                                        textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    
                                                    val priorityInfo = when(task.priority) {
                                                        TaskPriority.LOW -> "عادی" to com.example.ui.theme.GreyFuture
                                                        TaskPriority.MEDIUM -> "مهم" to com.example.ui.theme.OrangePending
                                                        TaskPriority.HIGH -> "فوری" to com.example.ui.theme.RedUrgent
                                                    }
                                                    
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(top = 2.dp)
                                                            .background(priorityInfo.second.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 1.dp)
                                                    ) {
                                                        Text(
                                                            priorityInfo.first,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = priorityInfo.second,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    }
                                                }
                                            }
                                            
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(onClick = { showTaskEditDialog = true }) {
                                                    Icon(
                                                        Icons.Default.Edit, 
                                                        contentDescription = "ویرایش تسک", 
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                IconButton(onClick = { viewModel.deleteTask(task) }) {
                                                    Icon(
                                                        Icons.Default.Delete, 
                                                        contentDescription = "حذف تسک", 
                                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }

                                        if (showTaskEditDialog) {
                                            EditTaskDialog(
                                                task = task,
                                                onDismiss = { showTaskEditDialog = false },
                                                onConfirm = { updatedTask ->
                                                    viewModel.updateTask(updatedTask)
                                                    showTaskEditDialog = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (showSectionTaskAddDialog) {
                            AddSectionTaskDialog(
                                section = section,
                                onDismiss = { showSectionTaskAddDialog = false },
                                onConfirm = { taskTitle, taskPriority ->
                                    viewModel.addTask(section.id, taskTitle, taskPriority, null)
                                    showSectionTaskAddDialog = false
                                }
                            )
                        }
                    }

                    TextButton(
                        onClick = { showAddSectionDialog = true },
                        modifier = Modifier.padding(start = 24.dp, top = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(4.dp))
                        Text(if (page.type == PageType.ECOMMERCE) "افزودن بخش/دسته‌بندی" else "افزودن بخش")
                    }
                }
            }
        }
    }

    if (showAddSectionDialog) {
        AddSectionDialog(
            isEcommerce = page.type == PageType.ECOMMERCE,
            onDismiss = { showAddSectionDialog = false },
            onConfirm = { title, isCat ->
                viewModel.addSection(page.id, title, isCat)
                showAddSectionDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPageDialog(onDismiss: () -> Unit, onConfirm: (String, PageType) -> Unit) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(PageType.GENERAL) }
    var expandedType by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("صفحه جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("نام صفحه") }, modifier = Modifier.fillMaxWidth())
                
                ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = !expandedType }) {
                    OutlinedTextField(
                        value = getPageTypeName(type), onValueChange = {}, readOnly = true, label = { Text("نوع صفحه") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) }
                    )
                    ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                        PageType.entries.forEach { pType ->
                            DropdownMenuItem(
                                text = { Text(getPageTypeName(pType)) }, 
                                onClick = { type = pType; expandedType = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, type) }) { Text("تایید") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("لغو") }
        }
    )
}

@Composable
fun AddSectionDialog(isEcommerce: Boolean, onDismiss: () -> Unit, onConfirm: (String, Boolean) -> Unit) {
    var title by remember { mutableStateOf("") }
    var isCategory by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("بخش جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("نام بخش") }, modifier = Modifier.fillMaxWidth())
                if (isEcommerce) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isCategory, onCheckedChange = { isCategory = it })
                        Text("این یک دسته‌بندی است")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, isCategory) }) { Text("تایید") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("لغو") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSectionTaskDialog(section: Section, onDismiss: () -> Unit, onConfirm: (String, TaskPriority) -> Unit) {
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.LOW) }
    var expandedPriority by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسک جدید در بخش ${section.title}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("عنوان تسک") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(expanded = expandedPriority, onExpandedChange = { expandedPriority = !expandedPriority }) {
                    OutlinedTextField(
                        value = getPriorityName(priority), onValueChange = {}, readOnly = true, label = { Text("اولویت") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) }
                    )
                    ExposedDropdownMenu(expanded = expandedPriority, onDismissRequest = { expandedPriority = false }) {
                        TaskPriority.entries.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(getPriorityName(p)) }, 
                                onClick = { priority = p; expandedPriority = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, priority) }) { Text("تایید") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("لغو") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(task: Task, onDismiss: () -> Unit, onConfirm: (Task) -> Unit) {
    var title by remember { mutableStateOf(task.title) }
    var priority by remember { mutableStateOf(task.priority) }
    var expandedPriority by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ویرایش تسک") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("عنوان تسک") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(expanded = expandedPriority, onExpandedChange = { expandedPriority = !expandedPriority }) {
                    OutlinedTextField(
                        value = getPriorityName(priority), onValueChange = {}, readOnly = true, label = { Text("اولویت") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) }
                    )
                    ExposedDropdownMenu(expanded = expandedPriority, onDismissRequest = { expandedPriority = false }) {
                        TaskPriority.entries.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(getPriorityName(p)) }, 
                                onClick = { priority = p; expandedPriority = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(task.copy(title = title, priority = priority)) }) { Text("بروزرسانی") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("لغو") }
        }
    )
}

private fun getPageTypeName(type: PageType): String {
    return when (type) {
        PageType.GENERAL -> "عمومی"
        PageType.ECOMMERCE -> "فروشگاهی (داستان و دسته‌ها)"
        PageType.BLOG -> "وبلاگ"
        PageType.DASHBOARD -> "داشبورد / پنل"
        PageType.LANDING -> "صفحه فرود (Landing)"
    }
}

private fun getPriorityName(priority: TaskPriority): String {
    return when (priority) {
        TaskPriority.LOW -> "عادی (کم)"
        TaskPriority.MEDIUM -> "مهم (متوسط)"
        TaskPriority.HIGH -> "فوری (بالا)"
    }
}
