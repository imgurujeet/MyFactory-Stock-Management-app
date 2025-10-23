package com.factory.myfactory.presentation.screens.coil

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.factory.myfactory.R
import com.factory.myfactory.data.models.CoilStockItem
import com.factory.myfactory.presentation.components.ListViewCard
import com.factory.myfactory.presentation.components.TableViewCard
import com.factory.myfactory.presentation.screens.coil.viewmodel.CoilViewModel


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CoilInventory(navHost: NavHostController ,viewModel: CoilViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    var tableView by rememberSaveable{ mutableStateOf(false) }
    val stockList by viewModel.entryStockList.collectAsState()
    val inventoryStockList by viewModel.inventoryStockList.collectAsState()

    val totalWeight = stockList.sumOf { it.coilStockItem.weight }

    // Inputs
    var selectedSize by rememberSaveable { mutableStateOf("") }
    val sizeOptions = listOf("1/2\"", "5/8\"", "3/4\"", "1\"", "1.5\"", "2\"", "2.5\"", "3\"")

    var selectedGauge by rememberSaveable { mutableStateOf("") }
    val gaugeOptions = listOf("16g", "18g", "20g", "22g", "24g","26g","28g")

    var selectedGrade by rememberSaveable { mutableStateOf("") }
    val gradeOptions = listOf("202", "304")

    var expandedSize by remember { mutableStateOf(false) }
    var expandedGauge by remember { mutableStateOf(false) }

    // Filtering the stock list based on selected values
    val filteredStock = stockList.filter { item ->
        val matchesSize = selectedSize.isEmpty() || item.coilStockItem.size == selectedSize
        val matchesGauge = selectedGauge.isEmpty() || item.coilStockItem.gauge == selectedGauge
        val matchesGrade = selectedGrade.isEmpty() || item.coilStockItem.grade == selectedGrade
        matchesSize && matchesGauge && matchesGrade
    }

    val filteredStockInventory = inventoryStockList.filter { item ->
        val matchesSize = selectedSize.isEmpty() || item.coilStockItem.size == selectedSize
        val matchesGauge = selectedGauge.isEmpty() || item.coilStockItem.gauge == selectedGauge
        val matchesGrade = selectedGrade.isEmpty() || item.coilStockItem.grade == selectedGrade
        matchesSize && matchesGauge && matchesGrade
    }



    Scaffold (
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ){
                Text(
                    text = "Coil Entry",
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
                val fontSize = when {
                    screenWidth < 360.dp -> 12.sp
                    screenWidth < 600.dp -> 14.sp
                    else -> 16.sp
                }

                val iconSize = when {
                    screenWidth < 360.dp -> 16.dp
                    screenWidth < 600.dp -> 20.dp
                    else -> 24.dp
                }

                val paddingSize = when {
                    screenWidth < 360.dp -> 4.dp
                    screenWidth < 600.dp -> 6.dp
                    else -> 8.dp
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = paddingSize),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {



                    // Size filter
                    ExposedDropdownMenuBox(
                        expanded = expandedSize,
                        onExpandedChange = { expandedSize = !expandedSize },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedSize,
                            onValueChange = {},
                            readOnly = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = fontSize),
                            label = { Text("Size", fontSize = fontSize) },
                            trailingIcon = {
                                if (selectedSize.isNotEmpty()) {
                                    IconButton(onClick = { selectedSize = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear selection",
                                            modifier = Modifier.size(iconSize)
                                        )
                                    }
                                } else {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSize)
                                }
                            },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSize,
                            onDismissRequest = { expandedSize = false }
                        ) {
                            sizeOptions.forEach { size ->
                                DropdownMenuItem(
                                    text = { Text(size, fontSize = fontSize) },
                                    onClick = {
                                        selectedSize = size
                                        expandedSize = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(paddingSize))

                    // Gauge filter
                    ExposedDropdownMenuBox(
                        expanded = expandedGauge,
                        onExpandedChange = { expandedGauge = !expandedGauge },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedGauge,
                            onValueChange = {},
                            readOnly = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = fontSize),
                            label = { Text("Gauge", fontSize = fontSize) },
                            trailingIcon = {
                                if (selectedGauge.isNotEmpty()) {
                                    IconButton(onClick = { selectedGauge = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear selection",
                                            modifier = Modifier.size(iconSize)
                                        )
                                    }
                                } else {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGauge)
                                }
                            },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        ExposedDropdownMenu(
                            expanded = expandedGauge,
                            onDismissRequest = { expandedGauge = false }
                        ) {
                            gaugeOptions.forEach { gauge ->
                                DropdownMenuItem(
                                    text = { Text(gauge, fontSize = fontSize) },
                                    onClick = {
                                        selectedGauge = gauge
                                        expandedGauge = false
                                    }
                                )
                            }
                        }
                    }
                }





                // UI for filters (could use DropdownMenu or FilterChips)
                Column (
                    Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Grade: ")

                        // Example: grade filter
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            gradeOptions.forEach { grade ->
                                FilterChip(
                                    selected = selectedGrade == grade,
                                    onClick = { selectedGrade = if (selectedGrade == grade) "" else grade },
                                    label = { Text(grade) }
                                )
                            }
                        }
                    }

                }


                if(!tableView) {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                   // contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredStock) { stockItemWithId ->
                        ListViewCard(
                            stockItemWithId = stockItemWithId, // actual data
                            viewModel = viewModel ,// pass full object (for id on delete)
                            navHost = navHost
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }

                }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Size",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gauge",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Grade",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Weight\n"+"(KGs)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
//                        Text(
//                            text = "Status",
//                            style = MaterialTheme.typography.bodyMedium,
//                            fontWeight = FontWeight.Bold
//                        )
                    }

                    val aggregatedStock = filteredStock
                        .groupBy { Triple(it.coilStockItem.size, it.coilStockItem.gauge, it.coilStockItem.grade) } // group by size, gauge, grade
                        .map { (key, items) ->
                            CoilStockItem(
                                size = key.first,
                                gauge = key.second,
                                grade = key.third,
                                weight = items.sumOf { it.coilStockItem.weight },
                                )
                        }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(filteredStockInventory) { index, stockItem ->
                            TableViewCard(
//                                stockItemWithId = CoilRepository.CoilStockItemWithId(
//                                    id = "aggregated_$index", // dummy ID for aggregated items
//                                    coilStockItem = stockItem
//                                ),
                                stockItemWithId = stockItem,
                                viewModel = viewModel,
                                navHost = navHost
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }



                }



            }

        }
    )

}

