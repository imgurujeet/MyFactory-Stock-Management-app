package com.factory.myfactory.presentation.screens.navigations

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.factory.myfactory.presentation.screens.cutpiece.CutPieceInventoryScreen
import com.factory.myfactory.presentation.screens.cutpiece.CutPieceOutflowScreen

@Composable
fun CutPieceNavGraph(navController: NavHostController=rememberNavController(),onBackToRoleScreen: () -> Unit = {}){

    Scaffold (
        bottomBar = {
            MyApp(navController,UserRole.CutPiece)
        }
    ){ innerPadding->

        NavHost(navController =navController, startDestination = Screen.CutPieceOutflow.route, modifier = Modifier.padding(innerPadding)){
            composable(Screen.CutPieceOutflow.route){
                CutPieceOutflowScreen(navController,onBack = onBackToRoleScreen)
            }

            composable(Screen.CutPieceInventory.route ){
                CutPieceInventoryScreen(navController)
            }

        }

    }


}