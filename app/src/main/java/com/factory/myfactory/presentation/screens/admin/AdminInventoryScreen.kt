package com.factory.myfactory.presentation.screens.admin

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.factory.myfactory.R
import com.factory.myfactory.presentation.components.ListViewCard
import com.factory.myfactory.presentation.components.TableViewCard
import com.factory.myfactory.presentation.screens.coil.viewmodel.CoilViewModel

@Composable
fun AdminInventoryScreen(navHost: NavHostController ,viewModel: CoilViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    var tableView by rememberSaveable{ mutableStateOf(false) }
    val stockList by viewModel.entryStockList.collectAsState()
    val totalWeight = stockList.sumOf { it.coilStockItem.weight }


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
                    text = "Stock Overview",
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
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding)
//                //.verticalScroll(rememberScrollState())
//            ){
//                Row(
//                    Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 8.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ){
//                    Card(
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(if(isLandscape) screenWidth*0.1f else screenHeight * 0.1f),
//                        shape = RoundedCornerShape(10.dp),
//                        colors = CardDefaults.cardColors(
//                            containerColor = Color.Green.copy(alpha = 0.1f)
//                        )
//
//                    ){
//                        Row(
//                            Modifier.fillMaxSize().padding(8.dp),
//                            horizontalArrangement = Arrangement.Center,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .weight(1f),
//                                verticalArrangement = Arrangement.Center, // center vertically
//                            ) {
//                                Text(
//                                    text = "${stockList.size}",
//                                    style = MaterialTheme.typography.headlineLarge,
//                                    color = MaterialTheme.colorScheme.onSurface,
//                                    fontWeight = FontWeight.SemiBold
//                                )
//                                Text(
//                                    text = "Total Coil",
//                                    style = MaterialTheme.typography.titleSmall,
//                                    color = MaterialTheme.colorScheme.onSurface,
//                                    fontWeight = FontWeight.Thin
//                                )
//                            }
//
//                            Icon(
//                                painter = painterResource(R.drawable.ic_coil),
//                                contentDescription = "Coil Image",
//                                tint = Color.Green.copy(alpha = 0.4f),
//                            )
//                        }
//
//
//
//
//                    }
//                    Card(
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(if(isLandscape) screenWidth*0.1f else screenHeight * 0.1f),
//                        shape = RoundedCornerShape(10.dp),
//                        colors = CardDefaults.cardColors(
//                            containerColor = Color.Red.copy(alpha = 0.1f)
//                        ),
//
//
//                        ){ Row(
//                        Modifier.fillMaxSize().padding(8.dp),
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically
//                    ){
//                        Column(
//                            modifier = Modifier
//                                .weight(1f),
//                            verticalArrangement = Arrangement.Center, // center vertically
//                            //horizontalAlignment = Alignment.CenterHorizontally // center horizontally
//                        ){
//                            Text(
//                                text = "$totalWeight",
//                                style = MaterialTheme.typography.headlineLarge,
//                                color = MaterialTheme.colorScheme.onSurface,
//                                fontWeight = FontWeight.SemiBold
//                            )
//                            Text(
//                                text = "Total Weight" ,
//                                style = MaterialTheme.typography.titleSmall,
//                                color = MaterialTheme.colorScheme.onSurface,
//                                fontWeight = FontWeight.Thin
//                            )
//                        }
//
//                        Icon(
//                            painter = painterResource(R.drawable.ic_weight),
//                            contentDescription = "Coil Image",
//                            tint = Color.Red.copy(alpha = 0.4f),
//                        )
//
//                    }
//
//
//                    }
//
//                }
//
//                if(!tableView) {
//
//                    LazyColumn(
//                        modifier = Modifier.fillMaxSize(),
//                        contentPadding = PaddingValues(8.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        items(stockList) { stockItemWithId ->
//                            ListViewCard(
//                                stockItemWithId = stockItemWithId, // actual data
//                                viewModel = viewModel ,// pass full object (for id on delete)
//                                navHost = navHost
//                            )
//                        }
//
//                    }
//                } else {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 8.dp, vertical = 4.dp),
//                        horizontalArrangement = Arrangement.SpaceEvenly,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "Size",
//                            style = MaterialTheme.typography.bodyMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Text(
//                            text = "Gauge",
//                            style = MaterialTheme.typography.bodyMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Text(
//                            text = "Grade",
//                            style = MaterialTheme.typography.bodyMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Text(
//                            text = "Weight\n"+"(KGs)",
//                            style = MaterialTheme.typography.bodyMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Text(
//                            text = "Status",
//                            style = MaterialTheme.typography.bodyMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//
//                    LazyColumn(
//                        modifier = Modifier.fillMaxSize(),
//                        contentPadding = PaddingValues(vertical = 8.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        items(stockList) { stockItemWithId ->
//                            TableViewCard(
//                                stockItemWithId = stockItemWithId, // actual data
//                                viewModel = viewModel ,// pass full object (for id on delete)
//                                navHost = navHost
//                            )
//                        }
//                    }
//
//
//                }
//
//



        }
    )

}





