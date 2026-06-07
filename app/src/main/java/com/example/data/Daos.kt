package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    fun getProjectById(id: String): Flow<Project?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)
}

@Dao
interface PageDao {
    @Query("SELECT * FROM pages WHERE projectId = :projectId ORDER BY createdAt ASC")
    fun getPagesForProject(projectId: String): Flow<List<Page>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: Page)

    @Update
    suspend fun updatePage(page: Page)

    @Delete
    suspend fun deletePage(page: Page)
}

@Dao
interface SectionDao {
    @Query("SELECT * FROM sections WHERE pageId = :pageId ORDER BY createdAt ASC")
    fun getSectionsForPage(pageId: String): Flow<List<Section>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: Section)

    @Delete
    suspend fun deleteSection(section: Section)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE sectionId = :sectionId ORDER BY createdAt ASC")
    fun getTasksForSection(sectionId: String): Flow<List<Task>>

    @Query("""
        SELECT tasks.* FROM tasks 
        INNER JOIN sections ON tasks.sectionId = sections.id
        INNER JOIN pages ON sections.pageId = pages.id
        WHERE pages.projectId = :projectId
        ORDER BY tasks.createdAt ASC
    """)
    fun getTasksForProject(projectId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC LIMIT 20")
    fun getRecentTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}
