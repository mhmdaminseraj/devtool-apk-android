package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class PageType {
    GENERAL, ECOMMERCE, BLOG, DASHBOARD, LANDING
}

@Entity(
    tableName = "pages",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Page(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val title: String,
    val type: PageType = PageType.GENERAL,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "sections",
    foreignKeys = [
        ForeignKey(
            entity = Page::class,
            parentColumns = ["id"],
            childColumns = ["pageId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Section(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val pageId: String,
    val title: String,
    val isCategory: Boolean = false, // To handle E-commerce Categories layer
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Section::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val sectionId: String,
    val title: String,
    val priority: TaskPriority = TaskPriority.LOW,
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
