package com.factory.myfactory.presentation.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.factory.myfactory.data.repositories.CoilRepository
import com.factory.myfactory.presentation.screens.coil.viewmodel.CoilViewModel
import com.factory.myfactory.presentation.screens.navigations.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TableViewCard(
    navHost: NavHostController,
    stockItemWithId : CoilRepository.CoilStockItemWithId,
    viewModel : CoilViewModel,
    modifier: Modifier = Modifier
){

    val stockItem = stockItemWithId.coilStockItem
    var showDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        showDialog = true
                    }
                )
            }
            .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier= Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            // Size
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Blue.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "${stockItem.size}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            // Gauge
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "${stockItem.gauge}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            // Grade
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .background(Color.Green.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "${stockItem.grade}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            // Weight + Timestamp
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .padding(start = 4.dp)
                    .background(Color.Transparent, shape = RoundedCornerShape(10.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ){
                Column {
                    Text(
                        text = stockItem.weight.toBigDecimal().toPlainString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        //overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
//                    Text(
//                        text = "${stockItem.timestamp.toTimeAgoString()}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        maxLines = 1,
//                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
//                    )
                }
            }

        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Action") },
            text = { Text("What do you want to do with this stock item?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navHost.navigate(Screen.UpdateStock.route + "/${stockItemWithId.id}")
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // Step 1: Show the confirmation dialog
                    showConfirmDialog = true
                }) {
                    Text("Delete")
                }
                if (showConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showConfirmDialog = false
                        },
                        title = { Text(text = "Confirm Delete") },
                        text = { Text("Are you sure you want to delete this item?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showConfirmDialog = false
                                    showDialog = false
                                    viewModel.deleteStock(stockItemWithId.id)
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showConfirmDialog = false
                                }
                            ) {
                                Text("No")
                            }
                        }
                    )
                }
            }
        )
    }
}



fun Long.toTimeAgoString(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val oneDay = 24 * 60 * 60 * 1000L
    val oneMonth = 30 * oneDay

    return when {
        diff >= oneMonth -> {
            // more than 1 month ago, show full date
            try {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                sdf.format(Date(this))
            } catch (e: Exception) {
                this.toString()
            }
        }
        diff >= oneDay -> {
            val daysAgo = (diff / oneDay).toInt()
            "$daysAgo days ago"
        }

        else -> "today"
    }
}