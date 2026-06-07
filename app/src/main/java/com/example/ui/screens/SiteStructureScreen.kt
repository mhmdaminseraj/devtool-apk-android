package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Page
import com.example.data.PageType
import com.example.viewmodel.DevTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteStructureScreen(
    projectId: String,
    viewModel: DevTrackViewModel,
    onBack: () -> Unit
) {
    val pages by viewModel.getPagesForProject(projectId).collectAsStateWithLifecycle(initialValue = emptyList())
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
            items(pages) { page ->
                PageItem(page = page, viewModel = viewModel)
            }
            if (pages.isEmpty()) {
                item {
                    Text("هنوز صفحه‌ای افزوده نشده. برای شروع، روی دکمه افزودن کلیک کنید.")
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
fun PageItem(page: Page, viewModel: DevTrackViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val sections by viewModel.getSectionsForPage(page.id).collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddSectionDialog by remember { mutableStateOf(false) }

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
                    Text(page.title, fontWeight = FontWeight.Bold)
                    Text(page.type.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { viewModel.deletePage(page) }) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف صفحه", tint = MaterialTheme.colorScheme.error)
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle"
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(section.title)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (section.isCategory) {
                                    Text("دسته‌بندی", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.deleteSection(section) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف بخش", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 32.dp))
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
                        value = type.name, onValueChange = {}, readOnly = true, label = { Text("نوع صفحه") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) }
                    )
                    ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                        PageType.entries.forEach { pType ->
                            DropdownMenuItem(text = { Text(pType.name) }, onClick = { type = pType; expandedType = false })
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
