package com.factory.myfactory.presentation.screens.navigations

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.factory.myfactory.presentation.screens.pipe.PipeEntryScreen
import com.factory.myfactory.presentation.screens.pipe.PipeInventoryScreen

@Composable
fun PipeNavGraph(navController : NavHostController= rememberNavController(),onBackToRoleScreen: () -> Unit = {}){

    Scaffold (
        bottomBar = {
            MyApp(navController, UserRole.Pipe)
        }
    ) { innerPadding->
        NavHost(navController= navController, startDestination = Screen.PipeEntry.route, modifier = Modifier.padding(innerPadding)){

            composable(Screen.PipeEntry.route){
                PipeEntryScreen(navController,onBack = onBackToRoleScreen)

            }
            composable(Screen.PipeInventory.route) {
                PipeInventoryScreen(navController)
            }

        }
    }

}

