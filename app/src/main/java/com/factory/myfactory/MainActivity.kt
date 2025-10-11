package com.factory.myfactory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.factory.myfactory.data.repositories.AuthRepository
import com.factory.myfactory.presentation.screens.navigations.AdminNavGraph
import com.factory.myfactory.presentation.screens.navigations.AppNavigation
import com.factory.myfactory.presentation.screens.navigations.CoilNavGraph
import com.factory.myfactory.presentation.screens.navigations.CutPieceNavGraph
import com.factory.myfactory.presentation.screens.navigations.PipeNavGraph
import com.factory.myfactory.presentation.screens.navigations.PipeOutflowNavGraph
import com.factory.myfactory.presentation.screens.navigations.ScrapCutPieceNavGraph
import com.factory.myfactory.presentation.screens.navigations.UserRole
import com.factory.myfactory.ui.theme.MyFactoryTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authRepository: AuthRepository  // Hilt will provide this

    override fun onCreate(savedInstanceState: Bundle?) {

        val db = Firebase.firestore
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHost= rememberNavController()
            var currentRole by rememberSaveable { mutableStateOf<UserRole?>(null) }
            MyFactoryTheme {
                Scaffold(

//                    bottomBar = { currentRole?.let{ role ->
//                        MyApp(navHost,role)
//                    }
//                    }
                )
                { innerPadding ->

                    val currentUserPhone = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.phoneNumber
                    if (!currentUserPhone.isNullOrEmpty()) {
                        authRepository.observeRegisteredUser(currentUserPhone) { regUser ->
                            if ( regUser?.active == false) {
                                authRepository.logout { success, _ ->
                                    if (success) currentRole = null
                                }
                            }
                        }

                    }
                    when (currentRole) {
                        null -> {
                            // Show Login flow
                            val rootNavController = rememberNavController()
                            AppNavigation(
                                navController = rootNavController,
                                onRoleSelected = { role -> currentRole = role }
                            )
                        }
                        UserRole.Admin -> AdminNavGraph(
                            onBackToRoleScreen = { currentRole = null }
                        )
                        UserRole.Coil -> CoilNavGraph(
                            onBackToRoleScreen = { currentRole = null }
                        )
                        UserRole.ScrapCutPieceOutFlow -> ScrapCutPieceNavGraph(
                            onBackToRoleScreen = { currentRole = null }
                        )
                        UserRole.Pipe -> PipeNavGraph(
                            onBackToRoleScreen = { currentRole = null }
                        )
                        UserRole.CutPiece -> CutPieceNavGraph(
                            onBackToRoleScreen = { currentRole = null }
                        )
                        UserRole.PipeOutflow -> PipeOutflowNavGraph(
                            onBackToRoleScreen = { currentRole = null }
                        )
                    }
                }
            }
        }
    }
}
