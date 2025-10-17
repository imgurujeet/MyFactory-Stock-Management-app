package com.factory.myfactory.presentation.screens.navigations


import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.factory.myfactory.presentation.screens.admin.AdminDashboardScreen
import com.factory.myfactory.presentation.screens.admin.AdminInventoryScreen
import com.factory.myfactory.presentation.screens.admin.RegisteredUsersScreen


//
//fun NavGraphBuilder.AdminNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
//    navigation(
//        route ="admin_graph",
//        startDestination = Screen.AdminDashboard.route,
//    ) {
//        composable(Screen.AdminDashboard.route) {
//            AdminDashboardScreen(navController)
//        }
//        composable(Screen.AdminInventory.route) {
//            AdminInventoryScreen(navController)
//        }
//
//    }
//}


//fun NavGraphBuilder.adminNavGraph(navController: NavHostController) {
//    navigation(
//        route = "d",
//        startDestination = Screen.AdminDashboard.route
//    ) {
//        composable(Screen.AdminDashboard.route) {
//            AdminDashboardScreen(navController)
//        }
//        composable(Screen.AdminInventory.route) {
//            AdminInventoryScreen(navController)
//        }
//    }
//}


@Composable
fun AdminNavGraph(
    navController: NavHostController = rememberNavController(),
    onBackToRoleScreen: () -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            if(currentRoute!=Screen.RegisteredUsersScreen.route){
                MyApp(navController, UserRole.Admin)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.AdminDashboard.route,
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                // bottom is NOT applied
            )
        ) {
            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(navController,onBack = onBackToRoleScreen)
            }
            composable(Screen.AdminInventory.route) {
                AdminInventoryScreen(navController)
            }
            composable (Screen.RegisteredUsersScreen.route){
                RegisteredUsersScreen(navController)

            }
        }
    }
}


