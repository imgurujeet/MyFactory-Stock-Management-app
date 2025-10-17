package com.factory.myfactory.presentation.screens.navigations

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
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
            startDestination = Screen.PipeOutflow.route,
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                // bottom is NOT applied
            )

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