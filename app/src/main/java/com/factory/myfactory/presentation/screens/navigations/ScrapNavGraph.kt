package com.factory.myfactory.presentation.screens.navigations

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.factory.myfactory.presentation.screens.scrap.ScrapCutPieceOutFlowScreen
import com.factory.myfactory.presentation.screens.scrap.ScrapInventoryScreen


@Composable
fun ScrapCutPieceNavGraph(navController: NavHostController = rememberNavController(),onBackToRoleScreen: () -> Unit = {}){

    Scaffold (
        bottomBar = {
            MyApp(navController, UserRole.ScrapCutPieceOutFlow)
        }
    ){ innerPadding->

        NavHost(
            navController=navController,
            startDestination = Screen.ScrapOutflow.route,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(Screen.ScrapOutflow.route){
                ScrapCutPieceOutFlowScreen(navController,onBack = onBackToRoleScreen)
            }
            composable(Screen.ScrapInventory.route){
                ScrapInventoryScreen(navController)

            }

        }

    }


}