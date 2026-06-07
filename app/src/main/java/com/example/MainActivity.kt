package com.example

// Touch to force rebuild
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DevTrackDatabase
import com.example.data.DevTrackRepository
import com.example.ui.components.AppNavigationShell
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DevTrackViewModel
import com.example.viewmodel.DevTrackViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = DevTrackDatabase.getDatabase(this)
        val repository = DevTrackRepository(database)

        setContent {
            val sharedPrefs = remember { getSharedPreferences("devtrack_preferences", MODE_PRIVATE) }
            val viewModel: DevTrackViewModel = viewModel(factory = DevTrackViewModelFactory(repository, sharedPrefs))
            val themeModeState by viewModel.themeMode.collectAsStateWithLifecycle()
            val isDark = when (themeModeState) {
                0 -> false
                1 -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    AppNavigationShell(navController = navController, viewModel = viewModel) {
                        NavHost(navController = navController, startDestination = Screen.Home) {
                            composable<Screen.Home> {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onProjectClick = { projectId ->
                                        navController.navigate(Screen.SiteStructure(projectId))
                                    }
                                )
                            }
                            composable<Screen.Projects> {
                                ProjectsScreen(
                                    viewModel = viewModel,
                                    onProjectClick = { projectId ->
                                        navController.navigate(Screen.SiteStructure(projectId))
                                    },
                                    onNewProject = {
                                        navController.navigate(Screen.NewProject)
                                    },
                                    onBack = {
                                        if (navController.previousBackStackEntry != null) {
                                            navController.popBackStack()
                                        } else {
                                            navController.navigate(Screen.Home) {
                                                popUpTo(Screen.Home) { inclusive = false }
                                            }
                                        }
                                    }
                                )
                            }
                            composable<Screen.Tasks> {
                                TasksScreen(
                                    viewModel = viewModel,
                                    onBack = {
                                        if (navController.previousBackStackEntry != null) {
                                            navController.popBackStack()
                                        } else {
                                            navController.navigate(Screen.Home) {
                                                popUpTo(Screen.Home) { inclusive = false }
                                            }
                                        }
                                    }
                                )
                            }
                            composable<Screen.Timeline> {
                                TimelineScreen(
                                    viewModel = viewModel,
                                    onBack = {
                                        if (navController.previousBackStackEntry != null) {
                                            navController.popBackStack()
                                        } else {
                                            navController.navigate(Screen.Home) {
                                                popUpTo(Screen.Home) { inclusive = false }
                                            }
                                        }
                                    }
                                )
                            }
                            composable<Screen.NewProject> {
                                NewProjectScreen(
                                    viewModel = viewModel,
                                    onNavigateToStructure = { projectId ->
                                        // Pop back to Projects and then go to Structure
                                        navController.popBackStack()
                                        navController.navigate(Screen.SiteStructure(projectId))
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable<Screen.SiteStructure> { backStackEntry ->
                                val structure: Screen.SiteStructure = backStackEntry.toRoute()
                                SiteStructureScreen(
                                    projectId = structure.projectId,
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable<Screen.Settings> {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onAboutClick = { navController.navigate(Screen.About) },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable<Screen.About> {
                                AboutScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
