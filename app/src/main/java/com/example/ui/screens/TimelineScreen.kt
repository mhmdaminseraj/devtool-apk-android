package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.components.AnimatedProgressBar
import com.example.ui.theme.*
import com.example.viewmodel.DevTrackViewModel
import kotlinx.coroutines.flow.flowOf
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    viewModel: DevTrackViewModel,
    onBack: () -> Unit
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    var selectedProject by remember(projects) { mutableStateOf<Project?>(projects.firstOrNull()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val pages by remember(selectedProject) {
        selectedProject?.let { viewModel.getPagesForProject(it.id) } ?: flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val tasks by remember(selectedProject) {
        selectedProject?.let { viewModel.getTasksForProject(it.id) } ?: flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("زمان‌بندی و برآورد اتمام", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (projects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        "هیچ پروژه‌ای تعریف نشده است",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "برای مشاهده خط زمانی و سناریوهای تخمین زمان، ابتدا از بخش پروژه‌ها یک پروژه جدید اضافه کنید.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Project Selector Row
                item {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedProject?.title ?: "انتخاب پروژه...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("پروژه فعال") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            projects.forEach { prj ->
                                DropdownMenuItem(
                                    text = { Text(prj.title) },
                                    onClick = {
                                        selectedProject = prj
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                selectedProject?.let { currentProject ->
                    // Summary and Estimates
                    item {
                        ThreeScenarioEstimatesCard(tasks = tasks)
                    }

                    // Milestones Title
                    item {
                        Text(
                            "مراحل اصلی پروژه (Milestones)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Vertical Timeline Milestones
                    if (pages.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                                    Text("صفحه‌ای در ساختار سایت این پروژه یافت نشد.")
                                }
                            }
                        }
                    } else {
                        items(pages) { page ->
                            val pageSections by viewModel.getSectionsForPage(page.id)
                                .collectAsStateWithLifecycle(initialValue = emptyList())
                            
                            val pageTasks = tasks.filter { task -> 
                                pageSections.any { sec -> sec.id == task.sectionId }
                            }
                            
                            MilestoneRow(
                                title = page.title,
                                subtitle = "نوع صفحه: ${getPageTypeName(page.type)}",
                                tasksCount = pageTasks.size,
                                completedTasksCount = pageTasks.count { it.isCompleted },
                                sections = pageSections
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThreeScenarioEstimatesCard(tasks: List<Task>) {
    val totalRemaining = tasks.count { !it.isCompleted }
    val highCount = tasks.count { !it.isCompleted && it.priority == TaskPriority.HIGH }
    val mediumCount = tasks.count { !it.isCompleted && it.priority == TaskPriority.MEDIUM }
    val lowCount = tasks.count { !it.isCompleted && it.priority == TaskPriority.LOW }

    // Multipliers for remaining days based on task priority
    // Optimistic, Most Likely, Pessimistic days/hours estimates
    val optimisticDays = (highCount * 0.5f + mediumCount * 1.0f + lowCount * 1.5f).roundToInt()
    val mostLikelyDays = (highCount * 1.0f + mediumCount * 2.0f + lowCount * 3.5f).roundToInt()
    val pessimisticDays = (highCount * 2.0f + mediumCount * 4.0f + lowCount * 6.0f).roundToInt()

    // High-contrast adaptive color calculations based on theme luminance
    val isDark = MaterialTheme.colorScheme.background.let {
        it.red * 0.2126f + it.green * 0.7152f + it.blue * 0.0722f < 0.5f
    }
    val activeGreen = if (isDark) NotionGreenDark else NotionGreenLight
    val activeOrange = if (isDark) NotionYellowDark else NotionYellowLight
    val activeRed = if (isDark) NotionRedDark else NotionRedLight

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "سناریوهای ۳گانه زمان تخمینی تکمیل",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "براساس $totalRemaining تسک باقی‌مانده و اولویت‌بندی آن‌ها:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Optimistic Card
                EstimateItemCard(
                    title = "خوش‌بینانه",
                    days = if (totalRemaining == 0) "0" else "$optimisticDays",
                    indicatorColor = activeGreen,
                    modifier = Modifier.weight(1f)
                )

                // Most-likely Card
                EstimateItemCard(
                    title = "محتمل‌ترین",
                    days = if (totalRemaining == 0) "0" else "$mostLikelyDays",
                    indicatorColor = activeOrange,
                    modifier = Modifier.weight(1f)
                )

                // Pessimistic Card
                EstimateItemCard(
                    title = "بدبینانه",
                    days = if (totalRemaining == 0) "0" else "$pessimisticDays",
                    indicatorColor = activeRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun EstimateItemCard(
    title: String,
    days: String,
    indicatorColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = indicatorColor.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, indicatorColor.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(indicatorColor, CircleShape)
            )
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "$days روز",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MilestoneRow(
    title: String,
    subtitle: String,
    tasksCount: Int,
    completedTasksCount: Int,
    sections: List<Section>
) {
    val progress = if (tasksCount == 0) 1.0f else completedTasksCount.toFloat() / tasksCount

    // High-contrast adaptive green
    val isDark = MaterialTheme.colorScheme.background.let {
        it.red * 0.2126f + it.green * 0.7152f + it.blue * 0.0722f < 0.5f
    }
    val activeGreen = if (isDark) NotionGreenDark else NotionGreenLight

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (progress == 1f) activeGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (progress == 1f) activeGreen else MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        )
                    }
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (progress == 1f) {
                    Icon(Icons.Default.CheckCircle, "تکمیل شده", tint = activeGreen)
                } else {
                    Text(
                        "${(progress * 100).roundToInt()}%",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            AnimatedProgressBar(progress = progress)

            if (sections.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "بخش‌های این صفحه: " + sections.joinToString("، ") { it.title },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
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
