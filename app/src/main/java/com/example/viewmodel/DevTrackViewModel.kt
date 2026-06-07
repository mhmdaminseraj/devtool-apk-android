package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DevTrackRepository
import com.example.data.Page
import com.example.data.Project
import com.example.data.Section
import com.example.data.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DevTrackViewModel(private val repository: DevTrackRepository) : ViewModel() {

    val projects: StateFlow<List<Project>> = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTasks: StateFlow<List<Task>> = repository.getRecentTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    suspend fun addProject(title: String, description: String = ""): String {
        val newProject = Project(title = title, description = description)
        repository.insertProject(newProject)
        return newProject.id
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch { repository.deleteProject(project) }
    }

    fun addPage(projectId: String, title: String, type: com.example.data.PageType) {
        viewModelScope.launch {
            repository.insertPage(Page(projectId = projectId, title = title, type = type))
        }
    }
    
    fun deletePage(page: Page) {
        viewModelScope.launch { repository.deletePage(page) }
    }

    fun addSection(pageId: String, title: String, isCategory: Boolean) {
        viewModelScope.launch {
            repository.insertSection(Section(pageId = pageId, title = title, isCategory = isCategory))
        }
    }
    
    fun deleteSection(section: Section) {
        viewModelScope.launch { repository.deleteSection(section) }
    }

    fun addTask(sectionId: String, title: String, priority: com.example.data.TaskPriority, dueDate: Long?) {
        viewModelScope.launch {
            repository.insertTask(Task(sectionId = sectionId, title = title, priority = priority, dueDate = dueDate))
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    // Getting related objects dynamically could be done with dedicated Flow properties 
    // or passing the Flows directly to Composables.
    fun getPagesForProject(projectId: String) = repository.getPagesForProject(projectId)
    fun getSectionsForPage(pageId: String) = repository.getSectionsForPage(pageId)
    fun getTasksForSection(sectionId: String) = repository.getTasksForSection(sectionId)
    fun getTasksForProject(projectId: String) = repository.getTasksForProject(projectId)
}

class DevTrackViewModelFactory(private val repository: DevTrackRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DevTrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DevTrackViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

