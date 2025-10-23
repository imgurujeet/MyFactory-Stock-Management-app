package com.factory.myfactory.presentation.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.factory.myfactory.data.repositories.CoilRepository
import com.factory.myfactory.presentation.screens.admin.viewmodel.AdminViewModel
import com.factory.myfactory.presentation.screens.coil.viewmodel.CoilViewModel

import com.factory.myfactory.presentation.screens.navigations.Screen

@Composable
fun ListViewCard(
    navHost: NavHostController,
    stockItemWithId : CoilRepository.CoilStockItemWithId,
    viewModel : CoilViewModel,
    modifier: Modifier = Modifier,
    adminViewModel: AdminViewModel = hiltViewModel()

) {
    val stockItem = stockItemWithId.coilStockItem
    var showDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current



    Card(
        modifier= Modifier.fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        showDialog = true
                    }
                )
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(stockItem.weight >= 1000)  Color.Green.copy(alpha = 0.5f) else if (stockItem.weight >= 500) MaterialTheme.colorScheme.surfaceContainerLow else  Color.Red.copy(alpha = 0.5f)
        )
    ){
        Card(
            modifier = modifier
                .fillMaxWidth()

                .padding(start = 6.dp),
            shape = RoundedCornerShape(10.dp),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {

            Row(
                Modifier.fillMaxWidth().padding(top=16.dp).padding(horizontal = 4.dp)
            ){
                Column {

                    Row{
                    Box(
                        modifier = Modifier.padding(end = 8.dp).background(
                            Color.Blue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        ).padding(8.dp),
                        contentAlignment = Alignment.Center

                    ) {
                        Text(
                            text = "${stockItem.size}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                    }

                    Box(
                        modifier = Modifier.padding(end = 8.dp).background(
                            Color.Gray.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        ).padding(8.dp),
                        contentAlignment = Alignment.Center

                    ) {
                        Text(
                            text = "${stockItem.gauge}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                    }
                    Box(
                        modifier = Modifier.background(
                            Color.Green.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        ).padding(8.dp),
                        contentAlignment = Alignment.Center

                    ) {
                        Text(
                            text = "${stockItem.grade}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                    }

                        Box(
                            modifier = Modifier.weight(0.5f).background(Color.Transparent,shape = RoundedCornerShape(10.dp)).padding(8.dp),
                            contentAlignment = Alignment.CenterEnd

                        ){

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ){
                                Text(
                                    text = stockItem.weight.toBigDecimal().toPlainString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " Kgs",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }


                        }
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            text = "Added: ${stockItem.timestamp.toDateString()} " + "(${stockItem.timestamp.toTimeAgoString()})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Added by: ${stockItem.entryUsername}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

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

// Helper extension to format timestamp
fun Long.toDateString(): String {
    return try {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(this))
    } catch (e: Exception) {
        this.toString()
    }
}





