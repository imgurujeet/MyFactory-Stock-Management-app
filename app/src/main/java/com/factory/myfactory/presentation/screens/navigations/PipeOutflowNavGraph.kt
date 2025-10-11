package com.factory.myfactory.presentation.screens.navigations

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.factory.myfactory.presentation.screens.pipeoutflow.PipeOutflowInventoryScreen
import com.factory.myfactory.presentation.screens.pipeoutflow.PipeOutflowScreen


@Composable
fun PipeOutflowNavGraph(navController: NavHostController=rememberNavController(),onBackToRoleScreen: () -> Unit = {}){


    Scaffold (
        bottomBar = {
            MyApp(
                navController, UserRole.PipeOutflow
            )

        }
    ){ innerPadding->
        NavHost(
            navController = navController,
            startDestination = Screen.PipeOutflow.route
        ){

            composable(route = Screen.PipeOutflow.route){
                // Call PipeOutflowScreen here
                PipeOutflowScreen(navHost = navController,onBack = onBackToRoleScreen)
            }

            composable (route = Screen.PipeOutflowInventory.route ){

                PipeOutflowInventoryScreen(navHost = navController,onBackToRoleScreen)

            }

        }
    }


}