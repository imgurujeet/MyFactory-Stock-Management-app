package com.factory.myfactory.presentation.components

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.factory.myfactory.data.models.PipeStock
import com.factory.myfactory.ui.theme.Typography

@Composable
fun PipeDetailCard(
    navHost: NavHostController,
    pipeStock: PipeStock,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ){
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "Entry", fontSize = Typography.titleMedium.fontSize, fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Back Icon",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                )

            }

            Row(
                modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Column {
                        Text(text = "Pipe Type", fontSize = Typography.titleSmall.fontSize)
                        Text(text = pipeStock.pipeType, fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize)
                    }
                    Column {
                        Text(text = "Pipe Size", fontSize = Typography.titleSmall.fontSize)
                        Text(text = pipeStock.pipeSize, fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize)
                    }

                    Column {
                        Text(text = "Apx.Weight", fontSize = Typography.titleSmall.fontSize)
                        Text(text = "${pipeStock.approxWeight} kg", fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Column {
                        Text(text = "Gauge", fontSize = Typography.titleSmall.fontSize)
                        Text(text = pipeStock.gauge, fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize)
                    }
                    Column {
                        Text(text = "Grade", fontSize = Typography.titleSmall.fontSize)
                        Text(text = pipeStock.grade, fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize)
                    }
                    Column {
                        Text(text = "Quantity", fontSize = Typography.titleSmall.fontSize)
                        Text(text = pipeStock.quantity.toString(), fontWeight = FontWeight.Bold, fontSize = Typography.titleMedium.fontSize, color = MaterialTheme.colorScheme.primary)
                    }
                }

            }

            Column {
                Text(text = "Entry By: ${pipeStock.entryUserName ?: "N/A" }", fontSize = Typography.titleSmall.fontSize)
                Text("${pipeStock.timestamp.toDateString()} (${pipeStock.timestamp.toTimeAgoString()})",fontSize = Typography.titleSmall.fontSize)
            }
        }


    }
}


@Composable
fun PipeDetailTableView(
    navHost: NavHostController,
    pipeStockList: List<PipeStock>,
) {

    // Sort states for each weight type
    var approxSort by remember { mutableStateOf<String?>(null) } // "high", "low", null
    val sortedList = when {
        approxSort != null -> when(approxSort) {
            "high" -> pipeStockList.sortedByDescending { it.approxWeight }
            "low" -> pipeStockList.sortedBy { it.approxWeight }
            else -> pipeStockList
        }
        else -> pipeStockList
    }


    Column {
        // Table header
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Type", fontWeight = FontWeight.Bold)
            Text("Size", fontWeight = FontWeight.Bold)
            Text("Quantity", fontWeight = FontWeight.Bold)
            Text("Appx.Wt.", fontWeight = FontWeight.Bold)

        }


        Divider()

        // Table rows
        LazyColumn {
            items(sortedList) { pipe ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(pipe.pipeType)
                    Text(pipe.pipeSize)
                    Text(pipe.quantity.toString())
                    Text(pipe.approxWeight.toString())
                }
                Divider()
            }
        }

    }
}

















