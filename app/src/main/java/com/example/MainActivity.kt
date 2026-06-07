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
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val viewModel: DevTrackViewModel = viewModel(factory = DevTrackViewModelFactory(repository))

                    AppNavigationShell(navController = navController) {
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
                                    }
                                )
                            }
                            composable<Screen.Tasks> {
                                TasksScreen(viewModel)
                            }
                            composable<Screen.Timeline> {
                                TimelineScreen()
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
                        }
                    }
                }
            }
        }
    }
}
