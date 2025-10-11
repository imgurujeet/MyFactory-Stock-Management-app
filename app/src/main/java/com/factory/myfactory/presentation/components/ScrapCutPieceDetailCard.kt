package com.factory.myfactory.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.factory.myfactory.data.models.CutPieceStock
import com.factory.myfactory.data.models.ScrapStock
import com.factory.myfactory.ui.theme.Typography
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun ScrapDetailCard(
    navHost: NavHostController,
    scrapStock: ScrapStock,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Entry", fontSize = Typography.titleMedium.fontSize, fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                )
            }


            Spacer(modifier = Modifier.padding(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.Center,){
                    Text(text = "Gauge", fontSize = Typography.titleSmall.fontSize)
                    Text(text = scrapStock.gauge, fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize)
                }
                Column (verticalArrangement = Arrangement.Center){
                    Text(text = "Grade", fontSize = Typography.titleSmall.fontSize)
                    Text(text = scrapStock.grade, fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize)
                }
                Column (verticalArrangement = Arrangement.Center){
                    Text(text = "Weight", fontSize = Typography.titleSmall.fontSize)
                    Text(text = "${scrapStock.weight} kg", fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize, color = MaterialTheme.colorScheme.primary)
                }
            }


            Column {
                Text(text = "Entry By: ${scrapStock.entryUserName ?: "N/A"}", fontSize = Typography.titleSmall.fontSize)
                Text("${scrapStock.timestamp.toDateString()} (${scrapStock.timestamp.toTimeAgoString()})",fontSize = Typography.titleSmall.fontSize)
            }
        }
    }
}


@Composable
fun ScrapDetailTableView(
    navHost: NavHostController,
    scrapStockList: List<ScrapStock>,
) {
    val scrollState = rememberScrollState()

    var sortOption by remember { mutableStateOf<String?>(null) } // "high", "low", or null
    var expanded by remember { mutableStateOf(false) }

    val sortedList = when (sortOption) {
        "high" -> scrapStockList.sortedByDescending { it.weight }
        "low" -> scrapStockList.sortedBy { it.weight }
        else -> scrapStockList
    }

    Column {
        // Table header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text("Gauge", fontWeight = FontWeight.Bold, )
            Text("Grade", fontWeight = FontWeight.Bold, )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Total Scrap Wt.", fontWeight = FontWeight.Bold,)
                // Dropdown icon
                Box {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Sort",
                        modifier = Modifier
                            .clickable { expanded = true }
                            .padding( 4.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by Highest") },
                            onClick = {
                                sortOption = "high"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Lowest") },
                            onClick = {
                                sortOption = "low"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Sorting") },
                            onClick = {
                                sortOption = null
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Divider()

        // Table rows
        LazyColumn {
            items(sortedList) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.gauge, )
                    Text(item.grade, )
                    Text("${item.weight} kg")
                }
                Divider()
            }
        }
    }
}





@Composable
fun CutPieceDetailCard(
    navHost: NavHostController,
    cutPieceStock: CutPieceStock
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Entry", fontSize = Typography.titleMedium.fontSize, fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                )
            }



                Spacer(modifier = Modifier.padding(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Gauge", fontSize = Typography.titleSmall.fontSize)
                        Text(text = cutPieceStock.gauge, fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize)
                    }
                    Column {
                        Text(text = "Grade", fontSize = Typography.titleSmall.fontSize)
                        Text(text = cutPieceStock.grade, fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize)
                    }
                    Column {
                        Text(text = "Weight", fontSize = Typography.titleSmall.fontSize)
                        Text(text = "${cutPieceStock.weight} kg", fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize, color = MaterialTheme.colorScheme.primary)
                    }
                }


            Column {
                Text(text = "Entry By: ${cutPieceStock.entryUserName ?: "N/A"}", fontSize = Typography.titleSmall.fontSize)
                Text("${cutPieceStock.timestamp.toDateString()} (${cutPieceStock.timestamp.toTimeAgoString()})",fontSize = Typography.titleSmall.fontSize)
            }
        }
    }
}


@Composable
fun CutPieceDetailTableView(
    navHost: NavHostController,
    cutPieceStock: List<CutPieceStock>,
) {
    var sortOption by remember { mutableStateOf<String?>(null) } // "high", "low", or null
    var expanded by remember { mutableStateOf(false) }

    val sortedList = when (sortOption) {
        "high" -> cutPieceStock.sortedByDescending { it.weight }
        "low" -> cutPieceStock.sortedBy { it.weight }
        else -> cutPieceStock
    }

    val scrollState = rememberScrollState()

    Column {
        // Table header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Gauge", fontWeight = FontWeight.Bold, )
            Text("Grade", fontWeight = FontWeight.Bold,)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Total CutPcs Wt.", fontWeight = FontWeight.Bold,)
                // Dropdown icon
                Box {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Sort",
                        modifier = Modifier
                            .clickable { expanded = true }
                            .padding( 4.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by Highest") },
                            onClick = {
                                sortOption = "high"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Lowest") },
                            onClick = {
                                sortOption = "low"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Sorting") },
                            onClick = {
                                sortOption = null
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Divider()

        // Table rows
        LazyColumn {
            items(sortedList) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.gauge, )
                    Text(item.grade,)
                    Text("${item.weight} kg",)
                }
                Divider()
            }
        }
    }
}


