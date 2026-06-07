package com.example.ui.screens

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable data object Home : Screen()
    @Serializable data object Projects : Screen()
    @Serializable data object Tasks : Screen()
    @Serializable data object Timeline : Screen()
    
    @Serializable data object NewProject : Screen()
    @Serializable data class SiteStructure(val projectId: String) : Screen()
    
    @Serializable data object Settings : Screen()
    @Serializable data object About : Screen()
}
