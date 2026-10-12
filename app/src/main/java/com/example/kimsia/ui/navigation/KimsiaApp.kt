package com.example.kimsia.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kimsia.ui.home.HomeScreen
import com.example.kimsia.ui.lesson.LessonScreen
import com.example.kimsia.ui.theme.Cream
import com.example.kimsia.ui.theme.EmeraldDark
import com.example.kimsia.ui.theme.EmeraldSoft
import com.example.kimsia.ui.theme.InkSoft

private enum class Tab(val route: String, val label: String, val icon: ImageVector) {
    Languages("languages", "Inicio", Icons.Filled.Home),
    Path("path", "Ruta", Icons.Filled.Map),
    Profile("profile", "Perfil", Icons.Filled.Person),
}

private const val LESSON_ROUTE = "lesson/{lessonId}"
private fun lessonRoute(id: Long) = "lesson/$id"

@Composable
fun KimsiaApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // La barra inferior se oculta dentro de una lección (pantalla inmersiva).
    val showBottomBar = Tab.entries.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    Scaffold(
        containerColor = Cream,
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // cada pantalla gestiona sus insets
        bottomBar = {
            if (showBottomBar) NavigationBar(containerColor = Color.White) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDark,
                            selectedTextColor = EmeraldDark,
                            indicatorColor = EmeraldSoft,
                            unselectedIconColor = InkSoft,
                            unselectedTextColor = InkSoft,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Tab.Path.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Tab.Languages.route) { PlaceholderScreen("Selector de idioma · Fase 2") }
            composable(Tab.Path.route) {
                HomeScreen(onLessonClick = { lessonId -> navController.navigate(lessonRoute(lessonId)) })
            }
            composable(
                route = LESSON_ROUTE,
                arguments = listOf(navArgument("lessonId") { type = NavType.LongType }),
            ) { entry ->
                LessonScreen(
                    lessonId = entry.arguments?.getLong("lessonId") ?: 0L,
                    onExit = { navController.popBackStack() },
                )
            }
            composable(Tab.Profile.route) { PlaceholderScreen("Perfil y logros · Fase 4") }
        }
    }
}

@Composable
private fun PlaceholderScreen(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text, color = InkSoft) }
}
