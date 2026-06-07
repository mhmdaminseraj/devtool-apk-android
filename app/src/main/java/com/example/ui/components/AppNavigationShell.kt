package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ui.screens.Screen
import com.example.viewmodel.DevTrackViewModel

data class BottomNavItem(val title: String, val route: Any, val icon: ImageVector)

@Composable
fun AppNavigationShell(
    navController: NavHostController,
    viewModel: DevTrackViewModel,
    content: @Composable () -> Unit
) {
    val items = listOf(
        BottomNavItem("داشبورد", Screen.Home, Icons.Default.Home),
        BottomNavItem("پروژه‌ها", Screen.Projects, Icons.Default.List),
        BottomNavItem("تسک‌ها", Screen.Tasks, Icons.Default.CheckCircle),
        BottomNavItem("زمان‌بندی", Screen.Timeline, Icons.Default.DateRange),
        BottomNavItem("تنظیمات", Screen.Settings, Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            // Determine if bottom bar should be shown
            val showBottomBar = currentDestination?.route?.let { route ->
                items.any { item -> route.contains(item.route::class.simpleName ?: "") }
            } == true

            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { 
                                it.route?.contains(item.route::class.simpleName ?: "") == true 
                            } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}
