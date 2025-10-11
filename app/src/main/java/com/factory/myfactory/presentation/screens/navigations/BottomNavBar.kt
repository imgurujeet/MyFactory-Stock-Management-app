package com.factory.myfactory.presentation.screens.navigations

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource

@Composable
fun MyApp(
    navController: NavHostController,
    currentRole: UserRole
) {
    // Define bottom nav items per role
    val items = when (currentRole) {
        UserRole.Admin -> listOf(
            Screen.AdminDashboard,
            Screen.AdminInventory
        )
        UserRole.Coil -> listOf(
            Screen.CoilEntry,
            Screen.CoilInventory
        )
        UserRole.ScrapCutPieceOutFlow -> listOf(
            Screen.ScrapOutflow,
            Screen.ScrapInventory
        )
        UserRole.Pipe -> listOf(
            Screen.PipeEntry,
            Screen.PipeInventory
        )
        UserRole.CutPiece -> listOf(
            Screen.CutPieceOutflow,
            Screen.CutPieceInventory
        )
        UserRole.PipeOutflow -> listOf(
            Screen.PipeOutflow,
            Screen.PipeOutflowInventory
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    screen.icon?.let { iconRes ->
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = screen.title
                        )
                    }
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}



