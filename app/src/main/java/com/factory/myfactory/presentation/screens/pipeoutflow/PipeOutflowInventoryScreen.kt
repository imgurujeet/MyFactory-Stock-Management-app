package com.factory.myfactory.presentation.screens.pipeoutflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.factory.myfactory.presentation.components.CutPieceDetailCard
import com.factory.myfactory.presentation.components.PipeDetailCard
import com.factory.myfactory.presentation.components.ScrapDetailCard
import com.factory.myfactory.presentation.screens.pipe.viemodel.PipeViewModel

@Composable
fun PipeOutflowInventoryScreen(navHost: NavHostController,onBack: () -> Unit,viewModel: PipeViewModel = hiltViewModel()){

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        viewModel.loadPipeOutflowEntries()
    }

    val pipeOutFlowEntries by viewModel.pipeOutflowEntry.collectAsState()

    val loading by viewModel.loading.collectAsState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 16.dp,vertical = 8.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Pipe Outflow Inventory",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = if(isLandscape) (screenHeight.value * 0.06f).sp else (screenWidth.value * 0.06f).sp, // dynamic font size
                    fontWeight = FontWeight.Bold,
                )
            }
        }

    ){innerPadding->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
            //.verticalScroll(rememberScrollState())
        ){

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(pipeOutFlowEntries) { pipeItem ->
                    PipeDetailCard(
                        navHost,
                        pipeStock = pipeItem.pipeStock
                    )
                }

            }

        }
    }
}