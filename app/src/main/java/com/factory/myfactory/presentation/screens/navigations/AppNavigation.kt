package com.factory.myfactory.presentation.screens.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.factory.myfactory.presentation.screens.auth.AppEntryAuthScreen
import com.factory.myfactory.presentation.screens.auth.LoginScreen



//@Composable
//fun AppNavigation(
//    navController: NavHostController,
//    modifier: Modifier = Modifier,
//    onRoleSelected: (UserRole) -> Unit
//) {
//    NavHost(
//        navController = navController,
//        startDestination = Login.route,
//        modifier = modifier
//    ) {
//        composable(Login.route) {
//            LoginScreen(
//                onLoginSuccess = { role ->
//                    onRoleSelected(role) // notify MainActivity
//                    when (role) {
//                        UserRole.Admin -> navController.navigate(Screen.AdminDashboard.route) {
//                            popUpTo(Login.route) { inclusive = true }
//                        }
//                        UserRole.Scrap -> navController.navigate(ScrapEntry.route) {
//                            popUpTo(Login.route) { inclusive = true }
//                        }
//                        UserRole.Pipe -> navController.navigate(PipeEntry.route) {
//                            popUpTo(Login.route) { inclusive = true }
//                        }
//                        UserRole.CutPiece -> navController.navigate(CutPieceEntry.route) {
//                            popUpTo(Login.route) { inclusive = true }
//                        }
//                    }
//                }
//            )
//
//        }
//        AdminNavGraph(navController)
//    }
//}


@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onRoleSelected: (UserRole) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AppEntryScreen.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    onRoleSelected(role) // inform MainActivity

                },
                navHost = navController

            )
        }
        composable (Screen.AppEntryScreen.route) {
            AppEntryAuthScreen(navController)
        }
    }
}




