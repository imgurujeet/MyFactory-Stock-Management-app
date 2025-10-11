package com.factory.myfactory.presentation.screens.pipe

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.factory.myfactory.R
import com.factory.myfactory.presentation.components.CutPieceDetailCard
import com.factory.myfactory.presentation.components.CutPieceDetailTableView
import com.factory.myfactory.presentation.components.ListViewCard
import com.factory.myfactory.presentation.components.PipeDetailCard
import com.factory.myfactory.presentation.components.PipeDetailTableView
import com.factory.myfactory.presentation.components.ScrapDetailCard
import com.factory.myfactory.presentation.components.ScrapDetailTableView
import com.factory.myfactory.presentation.components.SlidingSwitch
import com.factory.myfactory.presentation.screens.pipe.viemodel.PipeViewModel
import kotlin.text.compareTo

@Composable
fun PipeInventoryScreen(navHost: NavHostController,viewModel: PipeViewModel =hiltViewModel()){
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    var tableView by rememberSaveable{ mutableStateOf(false) }
    val viewList = rememberSaveable { listOf("Pipe","Scrap","CutPiece") }
    var viewOptionSelected by rememberSaveable { mutableStateOf(0) }



    LaunchedEffect(Unit) {
        viewModel.loadPipeEntries()
    }
    LaunchedEffect(Unit) {
        viewModel.loadPipeInventory()
    }
    LaunchedEffect(Unit) {
        viewModel.loadScrapEntries()
    }
    LaunchedEffect(Unit) {
        viewModel.loadScrapInventory()
    }
    LaunchedEffect(Unit) {
        viewModel.loadCutPieceEntries()
    }
    LaunchedEffect(Unit) {
        viewModel.loadCutPieceInventory()
    }
    val pipeEntries by viewModel.pipeEntries.collectAsState()
    val pipeInventory by viewModel.pipeInventory.collectAsState()
    val scrapEntries by viewModel.scrapEntries.collectAsState()
    val scrapInventory by viewModel.scrapInventory.collectAsState()
    val cutPieceEntries by viewModel.cutPieceEntries.collectAsState()
    val cutPieceInventory by viewModel.cutPieceInventory.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()


    Scaffold (
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ){
                Text(
                    text = "Stock Preview",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = if(isLandscape) (screenHeight.value * 0.06f).sp else (screenWidth.value * 0.06f).sp, // dynamic font size
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)

                )
                Icon(
                    painter = if (tableView) painterResource(R.drawable.ic_list) else painterResource(R.drawable.ic_table),
                    "Back arrow",
                    Modifier.align(Alignment.CenterEnd)
                        .clickable(
                            onClick = { tableView=!tableView
                                if (tableView) {
                                    Toast.makeText(context, "Table view enabled", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "List view enabled", Toast.LENGTH_SHORT).show()
                                }
                            }

                        )
                )
            }

        },
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                //.verticalScroll(rememberScrollState())
            ){

                SlidingSwitch(
                    viewList,
                    selectedIndex = viewOptionSelected,
                    onOptionSelected = { index ->
                        viewOptionSelected = index
                    }
                )
                // Show loading indicator
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                // Show success/error messages
                error?.let { Text(text = it, color = Color.Red) }
                successMessage?.let { Text(text = it, color = Color.Green) }

                Spacer(modifier = Modifier.height(16.dp))


                if(!tableView) {


                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {


                    when (viewOptionSelected) {
                        0 -> { // Pipe
                            items(pipeEntries) { pipeItem ->
                                PipeDetailCard(
                                    navHost,
                                    pipeStock = pipeItem.pipeStock
                                )
                            }
                        }
                        1-> {
                            items(scrapEntries) { scrapItem ->
                                ScrapDetailCard(
                                    navHost,
                                    scrapStock = scrapItem.scrapStock
                                )
                            }
                        }
                        2-> {
                            items(cutPieceEntries) { cutPieceEntry ->
                                CutPieceDetailCard(
                                    navHost,
                                    cutPieceStock = cutPieceEntry.cutPieceStock
                                )
                            }
                        }
                    }
                }

            }
                else {

                    // Table view
                    when (viewOptionSelected) {
                        0 -> PipeDetailTableView(
                            navHost = navHost,
                            pipeStockList = pipeInventory?.map { it.pipeStock } ?: emptyList()
                        )

                        1 -> ScrapDetailTableView(
                            navHost,
                            scrapStockList = scrapInventory?.map { it.scrapStock}  ?: emptyList()

                        )

                        2 -> CutPieceDetailTableView(
                            navHost,
                            cutPieceStock = cutPieceInventory?.map { it.cutPieceStock}  ?: emptyList()

                        )

                    }

                }

            }

        }
    )
}
