package com.example.data

import kotlinx.coroutines.flow.Flow

class DevTrackRepository(private val database: DevTrackDatabase) {
    private val projectDao = database.projectDao()
    private val pageDao = database.pageDao()
    private val sectionDao = database.sectionDao()
    private val taskDao = database.taskDao()

    // Projects
    fun getAllProjects(): Flow<List<Project>> = projectDao.getAllProjects()
    fun getProjectById(id: String): Flow<Project?> = projectDao.getProjectById(id)
    suspend fun insertProject(project: Project) = projectDao.insertProject(project)
    suspend fun updateProject(project: Project) = projectDao.updateProject(project)
    suspend fun deleteProject(project: Project) = projectDao.deleteProject(project)

    // Pages
    fun getPagesForProject(projectId: String): Flow<List<Page>> = pageDao.getPagesForProject(projectId)
    suspend fun insertPage(page: Page) = pageDao.insertPage(page)
    suspend fun updatePage(page: Page) = pageDao.updatePage(page)
    suspend fun deletePage(page: Page) = pageDao.deletePage(page)

    // Sections
    fun getSectionsForPage(pageId: String): Flow<List<Section>> = sectionDao.getSectionsForPage(pageId)
    suspend fun insertSection(section: Section) = sectionDao.insertSection(section)
    suspend fun deleteSection(section: Section) = sectionDao.deleteSection(section)

    // Tasks
    fun getTasksForSection(sectionId: String): Flow<List<Task>> = taskDao.getTasksForSection(sectionId)
    fun getTasksForProject(projectId: String): Flow<List<Task>> = taskDao.getTasksForProject(projectId)
    fun getRecentTasks(): Flow<List<Task>> = taskDao.getRecentTasks()
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}
