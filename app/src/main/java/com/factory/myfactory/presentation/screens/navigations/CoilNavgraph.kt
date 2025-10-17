package com.factory.myfactory.presentation.screens.navigations

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.factory.myfactory.presentation.screens.coil.CoilEntryScreen
import com.factory.myfactory.presentation.screens.coil.CoilInventory
import com.factory.myfactory.presentation.screens.coil.UpdateStockScreen
import com.factory.myfactory.presentation.screens.coil.viewmodel.CoilViewModel
import kotlin.collections.find

@Composable
fun CoilNavGraph(navController: NavHostController=rememberNavController(),onBackToRoleScreen: () -> Unit = {},viewModel: CoilViewModel = hiltViewModel()){

    Scaffold(
        bottomBar = {
            MyApp(navController, UserRole.Coil)
        }
    ){ innerPadding ->

        NavHost(navController= navController, startDestination = Screen.CoilEntry.route,
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                // bottom is NOT applied
            )

        ){

            composable(Screen.CoilEntry.route){
                CoilEntryScreen(navController,onBack = onBackToRoleScreen)

            }
            composable(Screen.CoilInventory.route) {
                CoilInventory(navController)
            }

            composable(
                route = Screen.UpdateStock.route + "/{docId}",
                arguments = listOf(navArgument("docId") { defaultValue = "" })
            ) { backStackEntry ->
                val docId = backStackEntry.arguments?.getString("docId") ?: ""

                // Access the stockList from ViewModel safely
                val stockList = viewModel.entryStockList.collectAsState().value

                val stockItemWithId = stockList.find { it.id == docId }

                stockItemWithId?.let {
                    UpdateStockScreen(navController, it, viewModel)
                }
            }

        }

    }


}