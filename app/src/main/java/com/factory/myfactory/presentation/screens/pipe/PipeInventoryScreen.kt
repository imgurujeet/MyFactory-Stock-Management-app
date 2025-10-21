package com.factory.myfactory.presentation.screens.pipe

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
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

    // --- Filter states ---
    var selectedGauge by rememberSaveable { mutableStateOf("") }
    var selectedGrade by rememberSaveable { mutableStateOf("") }
    var expandedGauge by remember { mutableStateOf(false) }

    val gaugeOptions = listOf("16g", "18g", "20g", "22g", "24g", "26g", "28g")
    val gradeOptions = listOf("202", "304")

    // --- Apply filter dynamically for current section ---
    val filteredPipeList = pipeEntries.filter {
        (selectedGauge.isEmpty() || it.pipeStock.gauge == selectedGauge) &&
                (selectedGrade.isEmpty() || it.pipeStock.grade == selectedGrade)
    }
    val filteredScrapList = scrapEntries.filter {
        (selectedGauge.isEmpty() || it.scrapStock.gauge == selectedGauge) &&
                (selectedGrade.isEmpty() || it.scrapStock.grade == selectedGrade)
    }
    val filteredCutPieceList = cutPieceEntries.filter {
        (selectedGauge.isEmpty() || it.cutPieceStock.gauge == selectedGauge) &&
                (selectedGrade.isEmpty() || it.cutPieceStock.grade == selectedGrade)
    }

    // --- LIST / TABLE View based on section ---
    val filteredPipeInventory = pipeInventory
        ?.map { it.pipeStock }
        ?.filter { (selectedGauge.isEmpty() || it.gauge == selectedGauge) &&
                (selectedGrade.isEmpty() || it.grade == selectedGrade) } ?: emptyList()

    val filteredScrapInventory = scrapInventory
        ?.map { it.scrapStock }
        ?.filter { (selectedGauge.isEmpty() || it.gauge == selectedGauge) &&
                (selectedGrade.isEmpty() || it.grade == selectedGrade) } ?: emptyList()

    val filteredCutPieceInventory = cutPieceInventory
        ?.map { it.cutPieceStock }
        ?.filter { (selectedGauge.isEmpty() || it.gauge == selectedGauge) &&
                (selectedGrade.isEmpty() || it.grade == selectedGrade) } ?: emptyList()





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

                Spacer(modifier = Modifier.height(12.dp)) 


                // --- FILTERS (common for all sections) ---

                when(viewOptionSelected) {
                    0->{

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Gauge Filter
                            ExposedDropdownMenuBox(
                                expanded = expandedGauge,
                                onExpandedChange = { expandedGauge = !expandedGauge },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = selectedGauge,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Gauge") },
                                    trailingIcon = {
                                        if (selectedGauge.isNotEmpty()) {
                                            Icon(
                                                imageVector = androidx.compose.material.icons.Icons.Default.Clear,
                                                contentDescription = "Clear",
                                                modifier = Modifier.clickable { selectedGauge = "" }
                                            )
                                        } else {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGauge)
                                        }
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .height(screenHeight * 0.06f), // Responsive thin height
                                    shape = RoundedCornerShape(8.dp)
                                )


                                ExposedDropdownMenu(
                                    expanded = expandedGauge,
                                    onDismissRequest = { expandedGauge = false },
                                ) {
                                    gaugeOptions.forEach { gauge ->
                                        DropdownMenuItem(
                                            text = { Text(gauge) },
                                            onClick = {
                                                selectedGauge = gauge
                                                expandedGauge = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.padding(6.dp))

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ){
                                // Grade Filter (Compact Chips)
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    // modifier = Modifier.weight(1f)
                                ) {
                                    gradeOptions.forEach { grade ->
                                        androidx.compose.material3.FilterChip(
                                            selected = selectedGrade == grade,
                                            onClick = {
                                                selectedGrade = if (selectedGrade == grade) "" else grade
                                            },
                                            label = { Text(grade, fontSize = 12.sp) }
                                        )
                                    }
                                }

                            }


                        }

                    }
                }

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
                            items(filteredPipeList) { pipeItem ->
                                PipeDetailCard(
                                    navHost,
                                    pipeStock = pipeItem.pipeStock
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                        1-> {
                            items(scrapEntries) { scrapItem ->
                                ScrapDetailCard(
                                    navHost,
                                    scrapStock = scrapItem.scrapStock
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }

                        }
                        2-> {
                            items(cutPieceEntries) { cutPieceEntry ->
                                CutPieceDetailCard(
                                    navHost,
                                    cutPieceStock = cutPieceEntry.cutPieceStock
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }

            }
                else {

                    // Table view
                    when (viewOptionSelected) {
                        0 -> PipeDetailTableView(navHost, pipeStockList = filteredPipeInventory)
                        1 -> ScrapDetailTableView(navHost, scrapStockList = scrapInventory ?.map { it.scrapStock }?:emptyList())
                        2 -> CutPieceDetailTableView(navHost, cutPieceStock = cutPieceInventory ?.map {it.cutPieceStock}?:emptyList())

                    }

                }

            }

        }
    )
}
