package com.factory.myfactory.presentation.screens.navigations

import com.factory.myfactory.R


sealed class Screen(val route: String, val title: String, val icon: Int?=null) {

    //App Entry Screen
    object AppEntryScreen : Screen("app_entry_auth","App Entry")

    //Role Choice
    object RoleChoiceScreen : Screen("role","Select Role")
    //login
    object Login : Screen ("login","Login")

    //Coil
    object CoilEntry : Screen("coil_entry", "Add Stock", R.drawable.add_stock)
    object CoilInventory : Screen("coil_inventory", "Inventory", R.drawable.ic_inventory)

    object UpdateStock : Screen("update_stock", "Update Stock")

    //Admin
    object AdminDashboard : Screen("admin_dashboard","Dashboard", R.drawable.ic_dashboard)
    object AdminInventory : Screen ("admin_inventory","Inventory",R.drawable.ic_inventory)
    object RegisteredUsersScreen : Screen ( "registered_users","Registered Users")

    //Pipe
    object PipeEntry : Screen("pipe_entry", "Add Stock",R.drawable.add_stock)
    object PipeInventory : Screen("pipe_inventory","Inventory",R.drawable.ic_inventory)

    //Scrap
    object ScrapOutflow : Screen("scrap_entry","Add Stock",R.drawable.add_stock)
    object ScrapInventory : Screen("scrap_inventory","Inventory",R.drawable.ic_inventory)

    //cut piece
    object  CutPieceOutflow : Screen("cut_piece_entry","Add Stock",R.drawable.add_stock)
    object  CutPieceInventory : Screen("cut_piece_inventory","Inventory",R.drawable.ic_inventory)


    //pipe outflow
    object PipeOutflow : Screen("pipe_outflow","Pipe Outflow",R.drawable.add_stock)
    object PipeOutflowInventory : Screen("pipe_outflow_inventory","Inventory",R.drawable.ic_inventory)



}

enum class UserRole {
    Admin, Coil, ScrapCutPieceOutFlow, Pipe, CutPiece, PipeOutflow
}
